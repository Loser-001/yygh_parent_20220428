package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.rabbit.constant.MqConst;
import com.atguigu.yygh.common.rabbit.service.RabbitService;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.order.mapper.OrderMapper;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.order.service.WeChatService;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.yygh.user.client.PatientFeignClient;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @create 2022-03-05 18:50
 */
@Service
public class OrderServiceImpl  extends ServiceImpl<OrderMapper,OrderInfo>  implements OrderService {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private  PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private WeChatService weChatService;

    @Override //预约统计
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //调用mapper方法得到数据
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);

        ///获取x需要的数据,日期的数据  --》 list集合
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //获取y需要的数据             --->  list集合
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    @Override//就诊通知
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        for(OrderInfo orderInfo : orderInfoList){
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }



    }

    @Override
    public Boolean cancelOrder(Long orderId) {
        //先获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //判断是否可以取消
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()){
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //调用医院接口实现预约取消
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updateCancelStatus");
        //根据医院接口返回的数据返回数据
        if(result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
             //是否支付 退款  判断当前订单是否可以取消
            if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                //已支付 进行退款
                boolean isRefund = weChatService.refund(orderId);
                if(!isRefund) { //退款失败
                    throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
            }
            //更改订单状态
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            baseMapper.updateById(orderInfo);
            //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("SMS_194640722");
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("status","取消预约成功,已退款！");
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        }
        return true;
    }

    //根据订单id查询订单详情信息
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword();  //医院名称
        Long patientId = orderQueryVo.getPatientId();//就诊人id
        String orderStatus = orderQueryVo.getOrderStatus();//订单状态
        String reserveDate = orderQueryVo.getReserveDate();//认证状态
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();//开始时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();//结束时间
        //对条件值进行非空判断
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)){
            queryWrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            queryWrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            queryWrapper.eq("reserve_date",orderStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, queryWrapper);
        //编号变成对应的值
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }

    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
    //生成挂号订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //获取订单就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        //获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        //判断当前时间是否还可以预约
        //当前时间不可以预约
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }
        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
        //添加到订单表里
        OrderInfo orderInfo = new OrderInfo();
        //把scheduleOrderVo 数据复制到orderInfo中去
        BeanUtils.copyProperties(scheduleOrderVo,orderInfo);
        //在向orderInfo中设置其它数据
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        //this.save(orderInfo);
        if(baseMapper.selectById(orderInfo) ==  null)
            baseMapper.insert(orderInfo);
        //调用医院接口，实现下单预约挂号的操作
        //设置调用医院接口需要参数，参数放到map集合中去
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        //请求医院系统接口
        JSONObject jsonResult = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        if(jsonResult.getInteger("code" ) == 200){
            JSONObject jsonObject = jsonResult.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //TODO 发送mq信息更新号源和短信通知
            //发送mq信息更新号源和短信通知
            //发送mq信息更新号源
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

             //进行短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("SMS_194640721");
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("status","预约成功！");
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);

            orderMqVo.setMsmVo(msmVo);
            //发送
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);


        } else {
            throw new YyghException(jsonResult.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();

    }
}

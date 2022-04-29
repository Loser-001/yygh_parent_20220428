package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.mapper.OrderMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-06 21:14
 */
@Service
public class OrderInfoServiceImpl   extends ServiceImpl<OrderInfoMapper,OrderInfo> implements OrderInfoService {

    @Autowired
    PatientFeignClient patientFeignClient;

    @Override
    public Map<String, Object> show(Long orderId) {
        Map<String, Object> map = new HashMap<>();
        OrderInfo orderInfo = this.packOrderInfo(this.getById(orderId));
        map.put("orderInfo", orderInfo);
        Patient patient
                =  patientFeignClient.getPatientById(orderInfo.getPatientId());
        map.put("patient", patient);
        return map;
    }
   //根据订单id查询订单
    private OrderInfo getById(Long orderId) {
        return baseMapper.selectById(orderId);
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

}

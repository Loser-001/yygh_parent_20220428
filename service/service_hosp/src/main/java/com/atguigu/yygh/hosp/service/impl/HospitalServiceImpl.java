package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Jerry
 * @create 2022-02-25 21:32
 */
@Service
public class HospitalServiceImpl implements HospitalService {


    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override//根据医院编号获取预约挂号详情
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    @Override //根据医院名称做查询
    public List<Hospital> findByHosName(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override//根据医院编号返回名称
    public String getByHosName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override////医院详情信息
    public Map<String,Object> getHospById(String id) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        result.put("hospital",hospital);
        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    @Override//更新医院上线功能
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Page<Hospital> selectHospitalPage(int page, int limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable =  PageRequest.of(page - 1,limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalSetQueryVo转换成Hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建example实例
        Example<Hospital> example = Example.of(hospital,matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        //List<Hospital> content = pages.getContent();
        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return pages;
    }
    //获取查询list集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省 市 地区
        String province = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("fullAddress",province + cityString + districtString);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    @Override
    public void save(Map<String, Object> paramMap) {
        //把map集合转换为对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //先判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        //如果存在，进行修改
         if(hospitalExist != null){
             hospital.setStatus(hospitalExist.getStatus());
             hospital.setCreateTime(hospitalExist.getCreateTime());
             hospital.setUpdateTime(new Date());
             hospital.setIsDeleted(0);
             hospitalRepository.save(hospital);
         }else{   //如果不存在，进行添加
             hospital.setStatus(0);
             hospital.setCreateTime(new Date());
             hospital.setUpdateTime(new Date());
             hospital.setIsDeleted(0);
             hospitalRepository.save(hospital);
         }




    }
}

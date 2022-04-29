package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-02-25 21:31
 */
public interface HospitalService {
    //上传医院接口
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    //医院列表(条件查询分页)
    Page<Hospital> selectHospitalPage(int page, int limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);
    //医院详情信息
    Map<String,Object> getHospById(String id);
    //获取医院名称
    String getByHosName(String hoscode);
   //根据医院名称做查询
    List<Hospital> findByHosName(String hosname);
    //根据医院编号获取医院预约挂号详情
    Map<String, Object> item(String hoscode);
}

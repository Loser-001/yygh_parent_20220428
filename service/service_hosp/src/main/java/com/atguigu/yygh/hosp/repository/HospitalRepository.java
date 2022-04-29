package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jerry
 * @create 2022-02-25 21:30
 */

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    //判断是否存在数据   --->  MongoRepository   方法的实现不需要我们写
    // ---->  只要我们按照规范 MongoRepository会帮助我们写好相当于 baseMapper里的sql查询语句
    Hospital getHospitalByHoscode(String hoscode);
    //根据医院名称做查询
    List<Hospital> findHospitalByHosnameLike(String hosname);
}

package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jerry
 * @create 2022-02-26 15:56
 */

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    //获取科室对应医院编码和科室编码的对象
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depname);
}

package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-02-26 15:57
 */
public interface DepartmentService {
    void save(Map<String, Object> paramMap);
   //查询科室接口
    Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    //根据医院编号，查询医院所有科室列表
    List<DepartmentVo> findDeptTree(String hoscode);
    //根据科室编号和医院编号查询科室名称
    String getDepName(String hoscode, String depcode);
    //科室
    Department getDepartment(String hoscode, String depcode);
}

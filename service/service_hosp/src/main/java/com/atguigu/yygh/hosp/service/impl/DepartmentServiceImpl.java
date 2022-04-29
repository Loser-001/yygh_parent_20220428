package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;

import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @create 2022-02-26 15:58
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override //返回科室对象
    public Department getDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null)
            return department;
        return null;
    }

    //根据科室编号和医院编号查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null)
          return department.getDepname();
        return null;
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建一个list,用于数据封装
        List<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        List<Department> departmentList = departmentRepository.findAll(example);
        //根据大科室分组  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 departmentMap
        for (Map.Entry<String,List<Department>> entry : departmentMap.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            //大科室对应下面的小科室
            List<Department> departmentList1 = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentList1.get(0).getBigname());
            //封账小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : departmentList1) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合中
                children.add(departmentVo2);
            }
            //把小科室放入大科室childrenList中去
            departmentVo.setChildren(children);
            //放入最终result中去
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号 和 科室编号查询
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    //分页查询部门信息
    @Override
    public Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数 0是第一页
        Pageable pageable = PageRequest.of(page - 1,limit);
        //将vo对象  ---->  department对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                                 .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                 .withIgnoreCase(true);
        Example<Department> example  = Example.of(department,matcher);
         Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap  -->  department对象
        String paramString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramString,Department.class);
        //根据医院编号  和  科室编号  进行查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepname());

        //判断
        if(departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{  //添加操作
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }



}

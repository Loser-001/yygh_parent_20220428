package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jerry
 * @create 2022-03-04 18:50
 */
public interface PatientService extends IService<Patient> {

    //获取就诊人列表
    List<Patient> findAllByUserId(Long userId);
    //根据id获取就诊人信息
    Patient getPatientById(Long id);
}

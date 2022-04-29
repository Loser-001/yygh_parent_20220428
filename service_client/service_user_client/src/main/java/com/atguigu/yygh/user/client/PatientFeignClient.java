package com.atguigu.yygh.user.client;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Jerry
 * @create 2022-03-05 19:06
 */

@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    //获取就诊人信息，根据id
    //根据就诊人id获取就诊人信息
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);

    //根据id获取就诊人信息
    @GetMapping("/api/user/patient/auth/getPatient/{patientId}")
    public Patient getPatientById(@PathVariable("patientId") Long patientId);
}

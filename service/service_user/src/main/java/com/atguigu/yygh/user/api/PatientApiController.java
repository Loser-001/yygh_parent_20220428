package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jerry
 * @create 2022-03-04 18:49
 */
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;
    //根据就诊人id获取就诊人信息
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id){
        Patient patientById = patientService.getPatientById(id);
        return patientById;
    }

    //获取就诊人列表
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        //得到登录就诊者的id
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllByUserId(userId);
        return Result.ok(list);
    }

    //添加就诊人
    @PostMapping("/auth/save")
    public Result savePatient(@RequestBody Patient patient,HttpServletRequest request){
        //获取当前登录id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }
    //根据id获取就诊人信息
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable  Long id){
        Patient patient = patientService.getPatientById(id);
        return Result.ok(patient);
    }
    //根据id获取就诊人信息
    @GetMapping("auth/getPatient/{patientId}")
    public Patient getPatientById(@PathVariable Long patientId){
        Patient patient = patientService.getPatientById(patientId);
        return patient;
    }
    //修改就诊人
    @PutMapping("auth/update")
    public Result updatePatient(@RequestBody  Patient patient){
      patientService.updateById(patient);
        return Result.ok();
    }
    //删除就诊人信息
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id){
        patientService.removeById(id);
        return  Result.ok();
    }
}
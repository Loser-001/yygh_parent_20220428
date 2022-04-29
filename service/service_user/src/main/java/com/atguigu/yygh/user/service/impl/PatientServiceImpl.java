package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jerry
 * @create 2022-03-04 18:50
 */
@Service
public class PatientServiceImpl  extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override//根据id和获取就诊人信息
    public Patient getPatientById(Long id) {
        return this.packPatient(baseMapper.selectById(id));
    }

    @Override //获取就诊人列表
    public List<Patient> findAllByUserId(Long userId) {
        //根据userId查询出所有就诊人信息列表
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id" ,userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        //下面操作通过远程调用，得到编码对应具体内容，查询数据字典表内容
        patientList.stream().forEach(item -> {
            //其它参数封装
            this.packPatient(item);
        });

        return patientList;
    }

    //Patient对象其它参数封装
    private Patient  packPatient(Patient patient) {
        //根据证件编码获取证件类型具体名字
       String certificatesTypeString =  dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getCertificatesType());
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }


}

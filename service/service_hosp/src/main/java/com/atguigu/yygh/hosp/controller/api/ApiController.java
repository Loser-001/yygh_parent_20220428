package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;


import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-02-25 21:34
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService  departmentService;

    @Autowired
    private ScheduleService scheduleService;


    //删除排班
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String scheduleId = (String) paramMap.get("hosScheduleId");
        //签名校验
        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");
        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.remove(hoscode,scheduleId);
        return Result.ok();
    }

    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //查询科室信息
        String depcode = (String) paramMap.get("depcode");
        //签名校验
        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");
        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        Integer page =  StringUtils.isEmpty(paramMap.get("page")) ? 1 :  Integer.parseInt((String) paramMap.get("page"));
        Integer limit =  StringUtils.isEmpty(paramMap.get("limit")) ? 1 :  Integer.parseInt((String) paramMap.get("limit"));
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setHoscode(depcode);
        //调用service方法
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }


    //上传排班接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //签名校验
        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String hospSign = (String) paramMap.get("sign");
        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);
        return Result.ok();
    }
    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //科室编号
        String depcode = (String) paramMap.get("depcode");
        //签名校验
        //根据传递过来的医院编码，查询数据库，查询签名
        String hospSign = (String) paramMap.get("sign");

        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院的编号
        String hoscode = (String) paramMap.get("hoscode");
        Integer page =  StringUtils.isEmpty(paramMap.get("page")) ? 1 :  Integer.parseInt((String) paramMap.get("page"));
        Integer limit =  StringUtils.isEmpty(paramMap.get("limit")) ? 1 :  Integer.parseInt((String) paramMap.get("limit"));
        //签名校验
        //根据传递过来的医院编码，查询数据库，查询签名
        String hospSign = (String) paramMap.get("sign");

        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //调用service方法
        Page<Department> pageModel = departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }


    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取到传递过来得医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //根据传递过来的医院编码，查询数据库，查询签名
        String hospSign = (String) paramMap.get("sign");

        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用departmentService
        departmentService.save(paramMap);
        return Result.ok();
    }

    //查询医院接口
    @PostMapping("hospital/show")
    public Result getHopital(HttpServletRequest request){
        //获取传递来医院信息
        //获取到传递过来得医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //根据传递过来的医院编码，查询数据库，查询签名
        String hospSign = (String) paramMap.get("sign");

        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String signKeyMDK5 = MD5.encrypt(signKey);
        if(!signKeyMDK5.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }



    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
          //获取到传递过来得医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院系统传递过来得签名
        String hospSign = (String) paramMap.get("sign");
        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");

        //查询数据库中对应的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //部署库查询出来的签名进行MD5加密
        String encrypt = MD5.encrypt(signKey);
        if(!encrypt.equals(hospSign)){
            throw  new YyghException(ResultCodeEnum.SIGN_ERROR);
        }


        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }
        //调用service方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
}

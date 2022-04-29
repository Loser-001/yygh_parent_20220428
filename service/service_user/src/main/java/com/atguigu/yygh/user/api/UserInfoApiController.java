package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-02 16:16
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userInfoService;

    //用户手机号登录接口
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
     Map<String,Object> info = userInfoService.LoginUser(loginVo);
        return Result.ok(info);
    }
    //用户认证
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //传递两个参数，第一个参数，用户id,第二个参数c串认证数据vo对象
       // System.out.println(userAuthVo);
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }
    //获取用户id信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }




}

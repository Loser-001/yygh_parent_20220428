package com.atguigu.yygh.common.utils;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jerry
 * @create 2022-03-04 15:21
 */

//获取当前用户信息的工具类
public class AuthContextHolder {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request){
        //先从header中获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }
    //获取当前用户名称
    public static String getUserName(HttpServletRequest request){
        //先从header中获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        String userName =  JwtHelper.getUserName(token);
        return userName;
    }
}

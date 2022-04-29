package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-04 20:19
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public  Result approval(@PathVariable Long userId, @PathVariable Integer authStatus){
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }


   //用户详情
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId){
        Map<String,Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }


    //用户锁定
    @GetMapping("lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId, @PathVariable Integer status){
        userInfoService.lock(userId,status);
        return Result.ok();
    }


    //用户列表接口(条件查询带分页)
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> pageParam = new Page<>(page,limit);
        IPage<UserInfo> pageModel =  userInfoService.selectPage(pageParam,userInfoQueryVo);
        return Result.ok(pageModel);
    }

}

package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-02 16:17
 */
public interface UserInfoService extends IService<UserInfo> {

   //根据用户信息返回登录状态信息
    Map<String, Object> LoginUser(LoginVo loginVo);
    //判断数据库中是否存在微信的扫描人信息
    //根据openid做判断
    UserInfo selectWxInfoOpenId(String openid);
    //用户认证
    void userAuth(Long userId, UserAuthVo userAuthVo);
   //用户列表接口(条件查询带分页)
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    //用户锁定
    void lock(Long userId,Integer status);
    //用户详情
    Map<String, Object> show(Long userId);
    //认证审批
    void approval(Long userId, Integer authStatus);
}

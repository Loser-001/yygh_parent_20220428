package com.atguigu.yygh.msm.controller;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Jerry
 * @create 2022-03-02 20:10
 */
@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码，如果获取成功，返回成功
        //redis key ： phone   value : 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code))
            return Result.ok();
        //如果从redis获取不到，生成验证码，通过整合短信服务进行发送
        //生成的验证码，通过整合短信验证码进行发送
        code = RandomUtils.getSixBitRandom();
        //通过整合云短信服务进行发送
        boolean isSend = msmService.send(phone,code);
        //是生成验证码放到redis里面，设置有效时间
        if(isSend){
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        }
        return Result.fail().message("发送短信失败！");
    }
}

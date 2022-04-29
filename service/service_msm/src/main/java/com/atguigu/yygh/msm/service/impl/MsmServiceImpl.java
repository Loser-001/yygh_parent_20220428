package com.atguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.msm.utils.RandomUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.netflix.client.ClientException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Jerry
 * @create 2022-03-02 20:11
 */
@Service
public class MsmServiceImpl implements MsmService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override//mq实现短信封装
    public boolean send(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        Map<String, Object> param = msmVo.getParam();
        String string = param.toString();
        if(!StringUtils.isEmpty(phone)){
            String code = "";
            if(redisTemplate.opsForValue().get(phone) == null) {
                code = RandomUtils.getSixBitRandom();
                Map map = new HashMap();
                map.put("code",code);
                msmVo.setParam(map);
            }else {
                code = (String) redisTemplate.opsForValue().get(phone);
            }
            boolean isSend = this.send(msmVo.getPhone(), string);
            //是生成验证码放到redis里面，设置有效时间
            if(isSend){
                redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
                return isSend;
            }
        }
        return false;
    }

    @Override//容联云实现短信发送
    public boolean send(String phone, String code) {
        //判断手机号是否为null
        if(StringUtils.isEmpty(phone))
            return false;
        //整合rly阿里云短信服务
        //设置相关参
        String ACCOUNT_ID = ConstantPropertiesUtils.ACCOUNT_ID;
        String APP_ID = ConstantPropertiesUtils.APP_ID;
        String AUTH_TOKEN = ConstantPropertiesUtils.AUTH_TOKEN;

        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.init("app.cloopen.com", "8883");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAccount(ACCOUNT_ID, AUTH_TOKEN);
        // 请使用管理控制台中已创建应用的APPID。
        restAPI.setAppId(APP_ID);
      //  String code = RandomStringUtils.randomNumeric(6);
        //设置需要发送的手机号和发送的验证码及过期时间
        result = restAPI.sendTemplateSMS(phone, "1", new String[]{code, "2"});

//        Map<String,Object> param = new HashMap<>();
//        param.put("code",code);
        if ("000000".equals(result.get("statusCode"))) {
            System.out.println(result);
            return true;
        } else {
            //异常返回输出错误码和错误信息
            String s = "错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg");
            System.out.println(s);
            return false;
        }
    }

//    private boolean send(String phone, Map<String,Object> param){
//
//    }
}

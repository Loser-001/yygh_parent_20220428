package com.atguigu.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtil;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信操作的接口
 * @author Jerry
 * @create 2022-03-03 15:15
 */
@Slf4j
@Controller
@RequestMapping("/api/ucenter/wx")
public class WexinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //2.回调的方法，得到扫码人的信息
    @GetMapping("callback")
    public String callback(String code,String state){
        //第一步，获取临时票据 code
        System.out.println("微信授权服务器回调。。。。。。");
        System.out.println("code:" + code);
        System.out.println("state:" + state);
        //第二步，拿着code和维系id和秘钥，请求微信固定地址，得到两个值
        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtil.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        //使用httpclient请求这个地址
        String result = null;
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo:" + accesstokenInfo);
            //从返回的字符串中获取两个值 openid  和 access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            //判断数据库中是否存在微信的扫描人信息
            //根据openid做判断
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if(userInfo == null){ //表示数据库中不存在微信信息
                //第三步 拿着openid 和 access_token 去请求微信地址，最终得到扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:" + resultInfo);
                JSONObject resultInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultInfoJson.getString("nickname");
                //用户头像
                String headimgurl = resultInfoJson.getString("headimgurl");

                //获取扫码人的信息添加到数据库中
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            //返回name和token字符串
            Map<String,String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid值是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机几号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //调转到前端页面中去
            return "redirect:" + ConstantWxPropertiesUtil.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
    }



    //1.生成微信扫描的二维码
     //返回生成二维码需要的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result getQrconnect(HttpSession session){
        Map<String,Object> map = null;
        String wxOpenRedirectUrl = null;
        try {
            map = new HashMap<>();
            wxOpenRedirectUrl = ConstantWxPropertiesUtil.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("appid", ConstantWxPropertiesUtil.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            map.put("redirect_uri",wxOpenRedirectUrl);
            map.put("state",System.currentTimeMillis() + "");
           // System.out.println(map);
            return   Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }



}

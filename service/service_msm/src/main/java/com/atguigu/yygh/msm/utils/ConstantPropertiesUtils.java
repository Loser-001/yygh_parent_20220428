package com.atguigu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @create 2022-03-02 20:00
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("8aaf07087e7b9872017f4a5278eb248e")
    private String accountId;

    @Value("8aaf07087e7b9872017f4a527a092494")
    private String appId;

    @Value("d8763292b20d46e6af716ef5a6e81258")
    private String authToken;

    public static String ACCOUNT_ID ;
    public static String APP_ID;
    public static String AUTH_TOKEN;

    @Override
    public void afterPropertiesSet() throws Exception {
        ACCOUNT_ID=accountId;
        APP_ID=appId;
        AUTH_TOKEN=authToken;
    }
}

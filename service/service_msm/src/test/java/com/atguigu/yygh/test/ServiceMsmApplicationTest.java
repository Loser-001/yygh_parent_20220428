package com.atguigu.yygh.test;

import com.atguigu.yygh.msm.utils.ConstantPropertiesUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Jerry
 * @create 2022-03-02 20:42
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@EnableDiscoveryClient
public class ServiceMsmApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMsmApplicationTest.class, args);
    }
}
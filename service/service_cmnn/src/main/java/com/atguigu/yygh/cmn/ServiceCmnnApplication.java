package com.atguigu.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author Jerry
 * @create 2022-02-21 10:21
 */
@SpringBootApplication
@ComponentScan(basePackages="com.atguigu") //不配这个会导致springboot默认只扫描当前微服务项目下的组件(和主启动类在同一个包下的)
                    //配这个basePackages="com.atguigu" 是为了将其它微服务下com.atguigu下的微服务bean扫描进来，比如操作redis的、swaggerde1
@EnableDiscoveryClient//注册到nacos上
public class ServiceCmnnApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ServiceCmnnApplication.class, args);
//        String[] definitionNames = run.getBeanDefinitionNames();
//        for (String name : definitionNames) {
//            System.out.println(name);
//        }
//        System.out.println(definitionNames.length);
    }
}


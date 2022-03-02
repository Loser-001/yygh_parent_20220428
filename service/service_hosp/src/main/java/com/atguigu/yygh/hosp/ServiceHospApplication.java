package com.atguigu.yygh.hosp;

//导入这两个包后，就会变成绿叶
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jerry
 * @create 2022-02-16 21:45
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
@EnableDiscoveryClient  //使用nacos进行注册
@EnableFeignClients(basePackages = "com.atguigu") //远程调用service_cmn_client
public class ServiceHospApplication {
    public static void main(String[] args) {
    SpringApplication.run(ServiceHospApplication.class,args);
    }
}

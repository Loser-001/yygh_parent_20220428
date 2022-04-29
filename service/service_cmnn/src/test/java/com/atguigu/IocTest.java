package com.atguigu;

import com.atguigu.yygh.cmn.ServiceCmnnApplication;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Jerry
 * @create 2022-04-29 11:55
 */
public class IocTest {
    @SuppressWarnings("resource")
    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServiceCmnnApplication.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name);
        }
    }

}

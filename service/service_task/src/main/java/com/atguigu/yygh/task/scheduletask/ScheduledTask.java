package com.atguigu.yygh.task.scheduletask;

import com.atguigu.yygh.common.rabbit.constant.MqConst;
import com.atguigu.yygh.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @create 2022-03-07 19:37
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    //在每天8点去执行方法，就医提醒   https://cron.qqe2.com/
    @Scheduled(cron = "0/30 * * * * ? ") //cron表达式，设置执行间隔   只支持6位
    public   void taskPatient(){
       rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}

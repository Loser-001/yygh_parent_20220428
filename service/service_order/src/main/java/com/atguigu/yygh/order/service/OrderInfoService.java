package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-06 21:13
 */
public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    Map<String,Object> show(Long orderId);

}

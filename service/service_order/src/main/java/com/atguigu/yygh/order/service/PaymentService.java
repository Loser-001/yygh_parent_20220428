package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-07 9:38
 */
public interface PaymentService extends IService<PaymentInfo> {
    //向支付记录表中添加信息
    void savePaymentInfo(OrderInfo order, Integer paymentType);
    //更新订单状态
    void paySucccess(String out_trade_no, Map<String, String> resultMap);
    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

}

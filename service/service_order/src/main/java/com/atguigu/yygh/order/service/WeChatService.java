package com.atguigu.yygh.order.service;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-07 9:31
 */
public interface WeChatService {
    //生成微信支付二维码
    Map<String, String> createNative(Long orderId);
    //调用微信接口实现支付状态
    Map<String, String> queryPayStatus(Long orderId);
    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);

}

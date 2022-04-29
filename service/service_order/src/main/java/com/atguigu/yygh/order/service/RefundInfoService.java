package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Jerry
 * @create 2022-03-07 12:08
 */
public interface RefundInfoService extends IService<RefundInfo> {
    /**
     * 保存退款记录
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}

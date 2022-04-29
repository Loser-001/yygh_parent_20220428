package com.atguigu.yygh.order.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-07 9:30
 */
@RestController
@RequestMapping("/api/order/weixin")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private PaymentService paymentService;

    //查询支付状态
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        //调用微信接口实现支付状态
        Map<String,String> resultMap = weChatService.queryPayStatus(orderId);
        //支付判断
        if(resultMap == null){
            return Result.fail().message("支付出错");
        }
        if("SUCCESS".equals(resultMap.get("trade_state"))){ //支付成功
            //更新订单状态
            String out_trade_no = resultMap.get("out_trade_no");//订单编码
            paymentService.paySucccess(out_trade_no,resultMap);
            return Result.ok().message("支付成功");
        }
        return  Result.ok().message("支付中");
    }

    //生成微信支付二维码
    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map<String,String> map = weChatService.createNative(orderId);
        return Result.ok(map);
    }


}

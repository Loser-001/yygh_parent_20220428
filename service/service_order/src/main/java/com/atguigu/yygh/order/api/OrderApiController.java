package com.atguigu.yygh.order.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;


import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-05 16:53
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }

    //取消预约
    @GetMapping("auth/cancelorder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        Boolean isCancle = orderService.cancelOrder(orderId);
        return Result.ok(isCancle);
    }


    //订单列表
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request){
        //设置当前用户id值
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }


    //根据订单id查询订单详情信息
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable  String orderId){
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    //生成挂号订单
    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrders(@ApiParam(name = "scheduleId", value = "排班id", required = true)
                              @PathVariable String scheduleId,
                             @ApiParam(name = "patientId", value = "就诊人id", required = true)
                             @PathVariable Long patientId){
        Long orderId = orderService.saveOrder(scheduleId,patientId);
        return Result.ok(orderId);
    }
}

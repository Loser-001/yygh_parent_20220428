package com.atguigu.yygh.order.client;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author Jerry
 * @create 2022-03-07 21:06
 */
@FeignClient(value = "service-order")
@Repository
public interface OrderFeignClient {

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}

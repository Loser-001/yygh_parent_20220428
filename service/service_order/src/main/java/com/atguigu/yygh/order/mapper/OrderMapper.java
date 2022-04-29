package com.atguigu.yygh.order.mapper;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jerry
 * @create 2022-03-05 18:51
 */

public interface OrderMapper extends BaseMapper<OrderInfo> {
    //查询预约统计数据的方法       //这里的  vo 是以orderCountQueryVo对象形式传到mapper中
    List<OrderCountVo> selectOrderCount(@Param("vo")OrderCountQueryVo orderCountQueryVo);

}

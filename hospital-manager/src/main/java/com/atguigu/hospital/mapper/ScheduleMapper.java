package com.atguigu.hospital.mapper;

import com.atguigu.hospital.model.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

}

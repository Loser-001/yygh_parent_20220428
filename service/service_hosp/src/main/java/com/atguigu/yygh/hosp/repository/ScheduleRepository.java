package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @create 2022-02-26 21:06
 */

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    //根据医院编号 和 排班编号查询
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
    //根据医院编号、科室编号和工作日期，查询排班详细信息
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
    //根据排班id获取排班数据
    Schedule getScheduleById(String scheduleId);
}

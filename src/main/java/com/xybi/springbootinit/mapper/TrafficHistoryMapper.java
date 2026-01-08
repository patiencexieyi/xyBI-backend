package com.xybi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface TrafficHistoryMapper extends BaseMapper<TrafficHistory> {
    /**
     * 查询指定城市近21天的历史数据（按日期升序）
     * @param city 城市名称
     * @param endDate 结束日期（默认当天）
     * @return 21天数据列表
     */
    @Select("SELECT * FROM trafficHistory " +
            "WHERE city = #{city} AND date >= DATE_SUB(#{endDate}, INTERVAL 20 DAY) AND date <= #{endDate} " +
            "ORDER BY date ASC")
    List<TrafficHistory> select21DaysByCity(@Param("city") String city, @Param("endDate") LocalDate endDate);
}
package com.xybi.springbootinit.service;

import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;

import java.time.LocalDate;
import java.util.List;

public interface TrafficHistoryService {
    /**
     * 批量插入数据（用于定时任务）
     */
    boolean batchInsert(List<TrafficHistory> dataList);

    /**
     * 查询指定城市近21天数据（自定义结束日期）
     */
    List<TrafficHistory> get21DaysData(String city, LocalDate endDate);

    /**
     * 生成指定日期的12个城市数据
     */
    List<TrafficHistory> generateDailyData(LocalDate date, boolean useRealWeather);
}
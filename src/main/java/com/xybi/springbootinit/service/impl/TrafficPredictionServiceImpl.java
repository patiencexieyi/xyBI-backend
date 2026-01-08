package com.xybi.springbootinit.service.impl;

import com.xybi.springbootinit.javaTopython.TrafficPredictionClient;
import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import com.xybi.springbootinit.service.TrafficHistoryService;
import com.xybi.springbootinit.service.TrafficPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficPredictionServiceImpl implements TrafficPredictionService {

    @Autowired
    private TrafficHistoryService trafficHistoryService;

    @Override
    public String predict7DaysTraffic(String city, String startDate) throws Exception {
        // 1. 查询21天历史数据
        List<TrafficHistory> historyList = trafficHistoryService.get21DaysData(city, LocalDate.parse(startDate));
        if (historyList.size() != 21) {
            throw new IllegalArgumentException("历史数据不足21天");
        }

        // 2. 构建21天历史数据的Map列表
        List<Map<String, Object>> historyDataList = new ArrayList<>();
        for (TrafficHistory po : historyList) {
            Map<String, Object> dataPointMap = new HashMap<>();
            dataPointMap.put("温度", po.getTemperature());
            dataPointMap.put("湿度", po.getHumidity());
            dataPointMap.put("风力", po.getWindPower());
            dataPointMap.put("降水量", po.getPrecipitation());
            dataPointMap.put("year", po.getYear());
            dataPointMap.put("month", po.getMonth());
            dataPointMap.put("day", po.getDay());
            dataPointMap.put("dayofweek", po.getDayOfWeek());
            dataPointMap.put("dayofyear", po.getDayOfYear());
            dataPointMap.put("weekofyear", po.getWeekOfYear());
            dataPointMap.put("quarter", po.getQuarter());
            dataPointMap.put("season", po.getSeason());
            dataPointMap.put("车流量_7天均值", po.getTraffic7dAvg());
            dataPointMap.put("车流量_3天趋势", po.getTraffic3dTrend());
            dataPointMap.put("车流量_月度峰值占比", po.getTrafficMonthPeakRatio());
            dataPointMap.put("车流量_7天中位数", po.getTraffic7dMedian());
            dataPointMap.put("车流量_7天标准差", po.getTraffic7dStd());

            // 分类特征
            dataPointMap.put("weather_type", po.getWeatherType());
            dataPointMap.put("is_holiday", po.getIsHoliday());
            dataPointMap.put("is_holiday_before", po.getIsHolidayBefore());
            dataPointMap.put("is_holiday_after", po.getIsHolidayAfter());
            dataPointMap.put("is_month_start", po.getIsMonthStart());
            dataPointMap.put("is_month_end", po.getIsMonthEnd());
            dataPointMap.put("is_weekend", po.getIsWeekend());
            dataPointMap.put("is_weekend_before", po.getIsWeekendBefore());
            dataPointMap.put("is_weekend_after", po.getIsWeekendAfter());

            historyDataList.add(dataPointMap);
        }

        // 3. 调用API
        return TrafficPredictionClient.predict(city, startDate, historyDataList);
    }
}

package com.xybi.springbootinit.model.dto.historyData;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("traffic_history")
public class TrafficHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String city;
    private LocalDate date;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal windPower; // 对应wind_power
    private BigDecimal precipitation;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer dayOfWeek; // 对应day_of_week
    private Integer dayOfYear; // 对应day_of_year
    private Integer weekOfYear; // 对应week_of_year
    private Integer quarter;
    private Integer season;
    private BigDecimal traffic7dAvg; // 对应traffic_7d_avg
    private BigDecimal traffic3dTrend; // 对应traffic_3d_trend
    private BigDecimal trafficMonthPeakRatio; // 对应traffic_month_peak_ratio
    private BigDecimal traffic7dMedian; // 对应traffic_7d_median
    private BigDecimal traffic7dStd; // 对应traffic_7d_std
    private String weatherType; // 对应weather_type
    private Integer isHoliday;
    private Integer isHolidayBefore;
    private Integer isHolidayAfter;
    private Integer isMonthStart;
    private Integer isMonthEnd;
    private Integer isWeekend;
    private Integer isWeekendBefore;
    private Integer isWeekendAfter;
    private LocalDateTime createTime;
}
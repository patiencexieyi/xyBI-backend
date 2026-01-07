package com.xybi.springbootinit.controller;

import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import com.xybi.springbootinit.service.TrafficHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor //为所有 final 修饰的字段和 @NonNull 注解的字段生成构造函数
@Slf4j
@RequestMapping("/traffic")
public class TrafficDataController {

    private final TrafficHistoryService trafficHistoryService;

    /**
     * 查询指定城市近21天数据（供预测使用）
     * 示例：http://localhost:8080/api/traffic/21days/北京
     */
    @GetMapping("/21days/{city}")
    public List<TrafficHistory> get21DaysData(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return trafficHistoryService.get21DaysData(city, endDate);
    }
}
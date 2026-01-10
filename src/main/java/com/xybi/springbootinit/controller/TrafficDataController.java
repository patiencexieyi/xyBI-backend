package com.xybi.springbootinit.controller;

import com.xybi.springbootinit.common.BaseResponse;
import com.xybi.springbootinit.common.ErrorCode;
import com.xybi.springbootinit.common.ResultUtils;
import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import com.xybi.springbootinit.service.TrafficHistoryService;
import com.xybi.springbootinit.service.TrafficPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor //为所有 final 修饰的字段和 @NonNull 注解的字段生成构造函数
@Slf4j
@RequestMapping("/traffic")
public class TrafficDataController {

    public final static String savePath = "D:\\毕设项目\\predictionResults";// 预测结果文件保存路径

    @Autowired
    private TrafficHistoryService trafficHistoryService;

    @Autowired
    private TrafficPredictionService trafficPredictionService;

    /**
     * 查询指定城市近21天数据（供预测使用）
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

    /**
     * 预测指定城市未来7天的车流量
     */
    @PostMapping("/predict7d")
    public BaseResponse<String> predict7DaysTraffic(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        try {
            String response = trafficPredictionService.predict7DaysTraffic(city, startDate.toString());
            return ResultUtils.success(response);
        } catch (Exception e) {
            log.error("预测7天车流量失败", e);
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "预测失败: " + e.getMessage());
        }
    }
}
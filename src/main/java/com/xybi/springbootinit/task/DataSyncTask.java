package com.xybi.springbootinit.task;

import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import com.xybi.springbootinit.service.TrafficHistoryService;
import com.xybi.springbootinit.utils.DataGenerateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据同步定时任务：每天凌晨0点执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncTask {

    private final TrafficHistoryService trafficHistoryService;

    /**
     * 定时任务表达式：0 0 0 * * ? （每天凌晨0点0分0秒执行）
     * cron表达式说明：秒 分 时 日 月 周 年（年可选）
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void syncDailyData() {
        log.info("开始执行每日交通数据同步任务...");
        List<TrafficHistory> Data = new ArrayList<>();
        LocalDate targetDate = LocalDate.now();
        try {
            //生成指定日期的12个城市数据
            Data = trafficHistoryService.generateDailyData(targetDate,true);

            // 执行批量插入
            boolean result = trafficHistoryService.batchInsert(Data);

            if (result) {
                log.info("{} 12个城市数据同步成功，共{}条", targetDate, Data.size());
            } else {
                log.error("{} 数据同步失败", targetDate);
            }
        } catch (Exception e) {
            log.error("{} 数据同步异常", targetDate, e);
        }
    }
}
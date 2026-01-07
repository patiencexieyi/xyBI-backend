package com.xybi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xybi.springbootinit.mapper.TrafficHistoryMapper;
import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import com.xybi.springbootinit.service.TrafficHistoryService;
import com.xybi.springbootinit.utils.DataGenerateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficHistoryServiceImpl extends ServiceImpl<TrafficHistoryMapper, TrafficHistory> implements TrafficHistoryService {

    private final TrafficHistoryMapper trafficHistoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsert(List<TrafficHistory> dataList) {
        return saveBatch(dataList);
    }

    @Override
    public List<TrafficHistory> get21DaysData(String city, LocalDate endDate) {
        List<TrafficHistory> dataList = trafficHistoryMapper.select21DaysByCity(city, endDate);
        return dataList;
    }

    @Override
    public List<TrafficHistory> generateDailyData(LocalDate date, boolean useRealWeather) {
        List<TrafficHistory> dataList = new ArrayList<>();
        for (String city : DataGenerateUtil.getCities()) {
            TrafficHistory data = useRealWeather ?
                    DataGenerateUtil.generateRealWeatherData(city, date) :
                    DataGenerateUtil.generateRealWeatherData(city, date);
            dataList.add(data);
        }
        return dataList;
    }
}
package com.xybi.springbootinit.service;

public interface TrafficPredictionService {

    String predict7DaysTraffic(String city, String startDate, String savePath) throws Exception;
}

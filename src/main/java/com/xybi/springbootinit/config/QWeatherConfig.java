package com.xybi.springbootinit.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 生成QWeather的业务类
 */
@Component // 交给Spring容器管理，才能注入配置
@Data
public class QWeatherConfig {

    @Value("${qWeather.private-key}")
    private String privateKey;

    @Value("${qWeather.app-id}")
    private String appId;

    @Value("${qWeather.secret-key}")
    private String secretKey;

    @Value("${qWeather.API_PATH}")
    private String API_PATH;

    @Value("${qWeather.WEATHER_BASE_URL}")
    private String WEATHER_BASE_URL;


}
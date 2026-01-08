package com.xybi.springbootinit.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xybi.springbootinit.config.QWeatherConfig;
import com.xybi.springbootinit.model.dto.historyData.TrafficHistory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class DataGenerateUtil {

    @Resource
    private static QWeatherConfig qWeatherConfig;

    // 12个城市列表
    private static final List<String> CITIES = Arrays.asList("北京", "上海", "广州", "深圳", "杭州", "成都", "重庆", "武汉", "西安", "南京", "郑州", "长沙");
    // 和风天气API配置
    private static final String API_PATH = qWeatherConfig.getAPI_PATH();
    private static final String WEATHER_BASE_URL = qWeatherConfig.getWEATHER_BASE_URL();


    /**
     * 生成指定日期+城市的历史数据（使用真实API数据）
     * 注意：和风天气免费版有调用次数限制（每天1000次），足够12个城市使用
     */
    public static TrafficHistory generateRealWeatherData(String city, LocalDate date) {
        TrafficHistory data = new TrafficHistory();
        data.setCity(city);
        data.setDate(date);

        // 1. 日期相关特征（纯Java API计算，无依赖）
        data.setYear(date.getYear());
        data.setMonth(date.getMonthValue());
        data.setDay(date.getDayOfMonth());
        // 星期几：0=周一，6=周日（适配Python模型）
        int dayOfWeek = date.getDayOfWeek().getValue() - 1;
        data.setDayOfWeek(dayOfWeek == -1 ? 6 : dayOfWeek);
        data.setDayOfYear(date.getDayOfYear());
        // 一年中的第几周
        data.setWeekOfYear(date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        // 季度
        data.setQuarter((date.getMonthValue() - 1) / 3 + 1);
        // 季节：1春(3-5)、2夏(6-8)、3秋(9-11)、4冬(12-2)
        int month = date.getMonthValue();
        if (month >= 3 && month <= 5) {
            data.setSeason(1);
        } else if (month >= 6 && month <= 8) {
            data.setSeason(2);
        } else if (month >= 9 && month <= 11) {
            data.setSeason(3);
        } else {
            data.setSeason(4);
        }
        // 是否月初/月末
        data.setIsMonthStart(date.getDayOfMonth() == 1 ? 1 : 0);
        data.setIsMonthEnd(date.lengthOfMonth() == date.getDayOfMonth() ? 1 : 0);
        // 是否周末
        boolean isWeekend = date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7;
        data.setIsWeekend(isWeekend ? 1 : 0);
        // 前1天/后1天是否周末
        LocalDate yesterday = date.minusDays(1);
        data.setIsWeekendBefore((yesterday.getDayOfWeek().getValue() == 6 || yesterday.getDayOfWeek().getValue() == 7) ? 1 : 0);
        LocalDate tomorrow = date.plusDays(1);
        data.setIsWeekendAfter((tomorrow.getDayOfWeek().getValue() == 6 || tomorrow.getDayOfWeek().getValue() == 7) ? 1 : 0);
        // 是否节假日（使用自定义节假日判断）
        boolean isHoliday = isHoliday(date);
        data.setIsHoliday(isHoliday ? 1 : 0);
        // 前1天/后1天是否节假日
        data.setIsHolidayBefore(isHoliday(yesterday) ? 1 : 0);
        data.setIsHolidayAfter(isHoliday(tomorrow) ? 1 : 0);

        // 2. 从API获取天气相关特征
        fetchWeatherDataFromAPI(data, city, date);

        // 3. 车流量相关特征（这里使用模拟数据，可以根据实际需求从其他API获取）
        ThreadLocalRandom random = ThreadLocalRandom.current();
        data.setTraffic7dAvg(BigDecimal.valueOf(random.nextDouble(0.4, 0.6)).setScale(4, RoundingMode.HALF_UP));
        data.setTraffic3dTrend(BigDecimal.valueOf(random.nextDouble(-0.1, 0.1)).setScale(4, RoundingMode.HALF_UP));
        data.setTrafficMonthPeakRatio(BigDecimal.valueOf(random.nextDouble(0.7, 0.9)).setScale(4, RoundingMode.HALF_UP));
        data.setTraffic7dMedian(BigDecimal.valueOf(random.nextDouble(0.45, 0.55)).setScale(4, RoundingMode.HALF_UP));
        data.setTraffic7dStd(BigDecimal.valueOf(random.nextDouble(0.03, 0.08)).setScale(4, RoundingMode.HALF_UP));

        // 打印获取到的数据
        printTrafficHistory(data);

        return data;
    }

    /**
     * 从API获取天气数据
     */
    private static void fetchWeatherDataFromAPI(TrafficHistory data, String city, LocalDate date) {
        String jwt = null;
        try {
            // 生成JWT
            jwt = Ed25519JWTUtil.generateJWT(qWeatherConfig.getPrivateKey(),
                    qWeatherConfig.getAppId(),
                    qWeatherConfig.getSecretKey()
            );
            // 调用和风天气API获取实时天气
            String cityId = getCityId(city);
            String url ="https://" + WEATHER_BASE_URL + API_PATH + "?location=" + cityId;
            // 构建带请求头的HTTP请求，包含Authorization头
            String resp = HttpRequest.get(url)
                    .header("User-Agent", "QWeather-Client/3.0")  // 添加User-Agent请求头
                    .header("Accept", "application/json")         // 添加Accept请求头
                    .header("Content-Type", "application/json")   // 添加Content-Type请求头
                    .header("Authorization", "Bearer " + jwt) // 添加Authorization请求头
                    .timeout(10000)                               // 设置超时时间10秒
                    .execute()
                    .body();

            log.info("API Response for {} on {}: {}", city, date, resp);

            JSONObject json = JSON.parseObject(resp);
            if ("200".equals(json.getString("code"))) {
                JSONObject now = json.getJSONObject("now");
                // 设置天气数据
                data.setTemperature(new BigDecimal(now.getString("temp")).setScale(1, RoundingMode.HALF_UP));
                data.setHumidity(new BigDecimal(now.getString("humidity")).setScale(1, RoundingMode.HALF_UP));
                data.setWindPower(new BigDecimal(now.getString("windScale")).setScale(1, RoundingMode.HALF_UP));

                // 处理降水量，API返回值可能是空字符串或null
                String precipStr = now.getString("precip");
                BigDecimal precipitation = precipStr != null && !precipStr.isEmpty() ?
                        new BigDecimal(precipStr).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                data.setPrecipitation(precipitation);

                // 天气类型映射（API返回编码转中文）
                String weatherCode = now.getString("text");
                data.setWeatherType(weatherCode);
            } else {
                log.error("API调用失败，错误码: {}, 城市: {}, 日期: {}", json.getString("code"), city, date);
            }
        } catch (Exception e) {
            log.error("获取{} {}天气数据失败，使用默认值", city, date, e);
        }
    }


    /**
     * 打印TrafficHistory数据
     */
    private static void printTrafficHistory(TrafficHistory data) {
        System.out.println("=== 交通历史数据 ===");
        System.out.println("城市: " + data.getCity());
        System.out.println("日期: " + data.getDate());
        System.out.println("年: " + data.getYear());
        System.out.println("月: " + data.getMonth());
        System.out.println("日: " + data.getDay());
        System.out.println("星期几: " + data.getDayOfWeek());
        System.out.println("一年中的第几天: " + data.getDayOfYear());
        System.out.println("一年中的第几周: " + data.getWeekOfYear());
        System.out.println("季度: " + data.getQuarter());
        System.out.println("季节: " + data.getSeason());
        System.out.println("是否月初: " + data.getIsMonthStart());
        System.out.println("是否月末: " + data.getIsMonthEnd());
        System.out.println("是否周末: " + data.getIsWeekend());
        System.out.println("前一日是否周末: " + data.getIsWeekendBefore());
        System.out.println("后一日是否周末: " + data.getIsWeekendAfter());
        System.out.println("是否节假日: " + data.getIsHoliday());
        System.out.println("前一日是否节假日: " + data.getIsHolidayBefore());
        System.out.println("后一日是否节假日: " + data.getIsHolidayAfter());
        System.out.println("温度: " + data.getTemperature());
        System.out.println("湿度: " + data.getHumidity());
        System.out.println("风力: " + data.getWindPower());
        System.out.println("降水量: " + data.getPrecipitation());
        System.out.println("天气类型: " + data.getWeatherType());
        System.out.println("7天平均交通流量: " + data.getTraffic7dAvg());
        System.out.println("3天交通趋势: " + data.getTraffic3dTrend());
        System.out.println("月峰值比例: " + data.getTrafficMonthPeakRatio());
        System.out.println("7天中位数交通流量: " + data.getTraffic7dMedian());
        System.out.println("7天交通流量标准差: " + data.getTraffic7dStd());
        System.out.println("==================");
    }

    // 实际需维护城市ID映射表（和风天气要求用城市ID调用）
    private static String getCityId(String city) {
        Map<String, String> cityIdMap = new HashMap<>();
        cityIdMap.put("北京", "101010100");
        cityIdMap.put("上海", "101020100");
        cityIdMap.put("广州", "101280101");
        cityIdMap.put("深圳", "101280601");
        cityIdMap.put("杭州", "101210101");
        cityIdMap.put("成都", "101270101");
        cityIdMap.put("重庆", "101040100");
        cityIdMap.put("武汉", "101200101");
        cityIdMap.put("西安", "101110101");
        cityIdMap.put("南京", "101190101");
        cityIdMap.put("郑州", "101180101");
        cityIdMap.put("长沙", "101250101");
        return cityIdMap.getOrDefault(city, "101010100");
    }

    /**
     * 判断是否为节假日（自定义节假日数据）
     */
    private static boolean isHoliday(LocalDate date) {
        // 这里可以实现自定义节假日逻辑，例如：
        // 1. 固定节假日（如国庆、春节等）
        // 2. 从配置文件或数据库读取节假日数据
        // 3. 使用第三方节假日API

        // 示例：简单的周末判断（实际应用中需要更复杂的节假日逻辑）
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // 示例：假设一些固定节假日
        if (month == 1 && day == 1) return true; // 元旦
        if (month == 10 && day >= 1 && day <= 7) return true; // 国庆
        if (month == 5 && day == 1) return true; // 劳动节

        // 也可以结合周末判断
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 获取12个城市列表
     */
    public static List<String> getCities() {
        return CITIES;
    }
}

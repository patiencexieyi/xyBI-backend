package com.xybi.springbootinit.javaTopython;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xybi.springbootinit.model.vo.PredictionResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficPredictionClient {
    private static final String API_URL = "http://192.168.203.128:8000/predict_7days";
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 调用预测API
     * @param city 目标城市
     * @param startDate 预测开始日期（YYYY-MM-DD）
     * @param historyDataList 21天历史数据（每个元素是Map，包含所有特征）
     * @return CSV文件保存路径
     * @throws IOException 网络/文件异常
     */
    public static String predict(String city, String startDate, List<Map<String, Object>> historyDataList) throws IOException {
        // 构建请求 - 使用Java 8兼容的方式创建Map
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("city", city);
        requestMap.put("start_date", startDate);
        requestMap.put("historical_data", historyDataList);

        String jsonRequest = OBJECT_MAPPER.writeValueAsString(requestMap);

        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request httpRequest = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

//        try (Response response = HTTP_CLIENT.newCall(httpRequest).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("API调用失败：" + response.code() + " - " + response.message());
//            }
//
//            // 使用响应类解析
//            String jsonResponse = response.body().string();
//            PredictionResponse predictionResponse = OBJECT_MAPPER.readValue(jsonResponse, PredictionResponse.class);
//
//            // 保存CSV文件
////            String fullSavePath = savePath + predictionResponse.getFilename();
////            try (FileWriter writer = new FileWriter(fullSavePath, false)) {
////                writer.write(predictionResponse.getCsvData());
////            }
//
//            return fullSavePath;
//        }
        try (Response response = HTTP_CLIENT.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API调用失败：" + response.code() + " - " + response.message());
            }

            // 解析响应并返回给前端
            String jsonResponse = response.body().string();
            return String.valueOf(OBJECT_MAPPER.readValue(jsonResponse, PredictionResponse.class));
        }
    }
}

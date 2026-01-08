package com.xybi.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictionResponse {
    @JsonProperty("csv_data")
    private String csvData;

    @JsonProperty("filename")
    private String filename;
}
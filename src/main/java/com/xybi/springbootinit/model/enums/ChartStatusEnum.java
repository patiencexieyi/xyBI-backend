package com.xybi.springbootinit.model.enums;

/**
 * 图表状态枚举
 */
public enum ChartStatusEnum {
    WAIT("wait", "等待中"),
    RUNNING("running", "执行中"),
    SUCCEED("succeed", "成功"),
    FAILED("failed", "失败");

    private final String value;
    private final String text;

    ChartStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

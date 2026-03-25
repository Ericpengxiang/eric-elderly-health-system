package com.health.common.constant;

/**
 * 体征阈值常量，用于录入后即时判断是否异常并生成预警
 */
public final class ThresholdConstants {

    private ThresholdConstants() {
    }

    public static final int SYSTOLIC_MAX = 140;
    public static final int SYSTOLIC_MIN = 90;
    public static final int DIASTOLIC_MAX = 90;
    public static final double BLOOD_SUGAR_MAX = 7.0;
    public static final double BLOOD_SUGAR_MIN = 3.9;
    public static final int HEART_RATE_MAX = 100;
    public static final int HEART_RATE_MIN = 60;
    public static final double BLOOD_OXYGEN_MIN = 95.0;
    public static final double TEMPERATURE_MAX = 37.5;
    public static final double TEMPERATURE_MIN = 36.0;
}

package cn.gyyx.im.utils;

/**
 * @author 邢少亚
 * @date 2022/8/3  10:39
 * @description 常用时间单位
 */
public interface DateTimeUnit {

    /**
     * 0秒
     */
    int ZERO_SECONDS = 0;

    /**
     * 一秒
     */
    int ONE_SECONDS = 1;
    /**
     * 一分钟
     */
    int ONE_MINUTE = 60;
    /**
     * 5分钟
     */
    int MINUTES_5 = ONE_MINUTE *5;
    /**
     * 10分钟
     */
    int MINUTES_10 = ONE_MINUTE *10;
    /**
     * 20分钟
     */
    int MINUTES_20 = 60 * 20;
    /**
     * 30分钟
     */
    int MINUTES_30 = 60 * 30;
    /**
     * 一小时
     */
    int ONE_HOUR = ONE_MINUTE *60;
    /**
     * 一天
     */
    int ONE_DAY = ONE_HOUR *24;
    /**
     * 一个月
     */
    int ONE_MONTH = ONE_DAY *30;
    /**
     * 一年
     */
    int ONE_YEAR = 365* ONE_DAY;
    /**
     * 18年
     */
    int YEAR_18 = 18* ONE_YEAR;
    /**
     * 20秒
     */
    int SECONDS_20 = 20;

    int minutes20 = 60 * 20;

    int minutes30 = 60 * 30;

}

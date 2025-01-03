package cn.gyyx.im.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 邢少亚
 * @date 2022/7/26/0026  16:30
 * @description 时间相关工具类
 */
public class DateTimeUtil {
    public final static String JSON_DATE_TIME_FORMAT_PATTERN_SUB = "MM/dd/yyyy HH:mm:ss";
    public final static DateTimeFormatter format1 = DateTimeFormatter.ofPattern(JSON_DATE_TIME_FORMAT_PATTERN_SUB);

    public final static String JSON_DATE_TIME_FORMAT_PATTERN2 = "yyyy年MM月dd日 HH:mm:ss";
    public final static String JSON_DATE_TIME_FORMAT_PATTERN3 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public final static String JSON_DATE_TIME_FORMAT_PATTERN4 = "yyyy-MM-dd";
    public final static String BindTime_FORMAT_PATTERN4 = "yyyy-MM-dd'T'HH:mm:ss";
    public final static DateTimeFormatter format2 = DateTimeFormatter.ofPattern(JSON_DATE_TIME_FORMAT_PATTERN2);

    public final static String yyyyMMdd = "yyyyMMdd";
    public final static String yyyyMMddHH = "yyyyMMddHH";
    public final static String yyyyMMddHH_zh = "yyyy年MM月dd日HH时mm分";
    public final static String yyyymmdd_zh="yyyy年MM月dd日";

    private final static String[] parsePatterns = {"yyyy-MM-dd","yyyy年MM月dd日",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};

    /**
     * 增加时间
     * @param date date
     * @param plusDays 增加天数
     * @param plusHours 增加小时
     * @param plusMinutes 增加分钟
     * @param plusSeconds 增加秒
     * @return Date
     */
    public static Date plusDays(Date date,int plusDays,int plusHours,int plusMinutes,int plusSeconds){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //天
        cal.add(Calendar.DATE, plusDays);
        //小时
        cal.add(Calendar.HOUR, plusHours);
        //分钟
        cal.add(Calendar.MINUTE, plusMinutes);
        //秒
        cal.add(Calendar.SECOND, plusSeconds);
        return cal.getTime();
    }

    /**
     * 获取格式化当前时间
     * @return Now_yyyyMMddHH
     */
    public static String getNow_yyyyMMddHH() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyyMMddHH);
        return simpleDateFormat.format(now);
    }

    /**
     * 格式化时间
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date,String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 校验字符串是不是时间日期格式
     * @param string string
     * @return Date
     */
    public static Date parseDate(String string) {
        if (string == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(string, parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 两个时间比较
     * @param date1 时间大的
     * @param date2 时间小的
     * @return date1 - date2
     */
    public static long betweenTime(Date date1,Date date2){
        return date1.getTime() - date2.getTime();
    }

    /**
     * 两个时间的比较
     * @param date1 时间大的
     * @param date2 时间小的
     * @param unit 返回的时间单位，单位秒，如1分钟，则返回两者相差多少分钟，四舍五入
     * @return 间隔
     */
    public static int betweenTime(Date date1,Date date2,int unit){
        long l = betweenTime(date1, date2);
        int seconds = (int)(l / 1000);
        return seconds/unit;
    }

    /**
     * 获取今天结束时间
     * @return 今天结束时间
     */
    public static Date getTodayEndTime() {
        Calendar ca=Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        ca.add(Calendar.DAY_OF_MONTH, 1);
        return ca.getTime();
    }

    public static long getNow() {
        return System.currentTimeMillis();
    }
    /**
     *  转换当前时间为yyyyMMddHHmmss格式化
     * @param
     * @return java.lang.String
     * @author leeway
     * @date 2023/6/20/020 13:59
     */
    public static String getCurrentTime(Date currentDate) {

        // 指定格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        // 格式化日期
        String formattedTime = dateFormat.format(currentDate);

        return formattedTime;
    }

    /**
     * 获取去年的今天
     * @return
     */
    public static Date getLastYearToDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }
}

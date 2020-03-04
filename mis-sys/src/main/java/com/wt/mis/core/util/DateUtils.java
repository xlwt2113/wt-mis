package com.wt.mis.core.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 处理时间的工具类
 *
 * @author mac
 */
@Slf4j
public class DateUtils {

    private static final DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter datePathFormatter = DateTimeFormatter.ofPattern("yyyyMM/dd");
    private static final HashMap<DayOfWeek, List<String>> weekMap = new HashMap<>();

    static {
        weekMap.put(DayOfWeek.MONDAY, Arrays.asList("一", "周一", "星期一"));
        weekMap.put(DayOfWeek.TUESDAY, Arrays.asList("二", "周二", "星期二"));
        weekMap.put(DayOfWeek.WEDNESDAY, Arrays.asList("三", "周三", "星期三"));
        weekMap.put(DayOfWeek.THURSDAY, Arrays.asList("四", "周四", "星期四"));
        weekMap.put(DayOfWeek.FRIDAY, Arrays.asList("五", "周五", "星期五"));
        weekMap.put(DayOfWeek.SATURDAY, Arrays.asList("六", "周六", "星期六"));
        weekMap.put(DayOfWeek.SUNDAY, Arrays.asList("日", "周日", "星期日"));
    }

    private DateUtils() {
    }

    /**
     * 将中文转化为星期
     *
     * @param chinese
     * @return
     */
    public static DayOfWeek parseDayOfWeek(String chinese) {

        log.info("parseDayOfWeek - > {}", chinese);
        //java自带的日期枚举类，同类的还有 Month 等
        return weekMap.entrySet().stream().filter(entry -> entry.getValue().contains(chinese)).findFirst()
                .orElseThrow(UnsupportedOperationException::new).getKey();
    }

    /**
     * 生成星期选择菜单
     *
     * @param style
     * @return
     */
    public static List<Map<String, String>> getDayOfWeekSelect(TextStyle style) {
        //style支持各种长短格式 后台接受select框的value值可以直接生成DayOfWeek枚举类
        return Stream.of(DayOfWeek.values()).map(week -> new HashMap<String, String>() {{
            put(week.name(), week.getDisplayName(style, Locale.CHINESE));
        }}).collect(Collectors.toList());
    }

    public static String parseToChinese(DayOfWeek dayOfWeek, TextStyle style) {
        return dayOfWeek.getDisplayName(style, Locale.CHINESE);
    }

    public static String dateFormat(Date date){
        return dateFormat(date,"yyyy-MM-dd");
    }

    public static String dateTimeFormat(Date date){
        return dateFormat(date,"yyyy-MM-dd HH:mm:ss");
    }

    public static String timeFormat(Date date){
        return dateFormat(date,"HH:mm:ss");
    }

    public static String dateFormat(Date date,String strFormat){
        SimpleDateFormat sf = new SimpleDateFormat(strFormat);
        String str = sf.format(date);
        return str;
    }

    public static String parseToChinese(DayOfWeek dayOfWeek) {
        return parseToChinese(dayOfWeek, TextStyle.FULL);
    }

    public static String defaultDateTimeFormatter(LocalDateTime date) {
        return date.format(defaultDateFormatter);
    }

    public static DateTimeFormatter defaultDateTimeFormatter() {
        return defaultDateFormatter;
    }

    public static String datePathFormat(LocalDate date) {
        return date.format(datePathFormatter);
    }

    public static DateTimeFormatter datePathFormat() {
        return datePathFormatter;
    }

    public static Date of(java.sql.Date date) {
        return date;
    }

    public static Date of(Timestamp timestamp) {
        return timestamp;
    }

    public static long millisecond() {
        return System.currentTimeMillis();
    }

    public static long nanosecond() {
        return System.nanoTime();
    }


}

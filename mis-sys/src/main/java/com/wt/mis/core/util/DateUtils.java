package com.wt.mis.core.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.ParseException;
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

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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

    //日期天数加减
    public static Date dayAddNum(Date time, Integer num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DATE, num);
        Date newTime = calendar.getTime();
        return newTime;
    }

    //时间小时加减
    public static Date hourAddNum(Date time, Integer num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR, num);
        Date newTime = calendar.getTime();
        return newTime;
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

    /**
     * 开发人：zhangbo
     * 开发日期：2007-05-31
     * 开发时间：16:20
     * 功能描述：返回当前日期是周几，分别返回日,一,二,三,四,五,六
     * 方法的参数和返回值：
     */
    public static String getWeek() {
        return getWeek(new Date());
    }

    /**
     * 开发人： 王涛
     * 开发时间： 2011-6-23 上午10:14:07
     * 功能描述：返回指定日期是周几，分别返回日,一,二,三,四,五,六
     * 方法的参数和返回值
     *
     * @param d
     * @return String
     * ==================================
     * 修改历史
     * 修改人        修改时间      修改原因及内容
     * <p>
     * ==================================
     */
    public static String getWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int posOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        posOfWeek--;
        String[] ary = "日,一,二,三,四,五,六".split(",");
        return ary[posOfWeek];
    }

    public static int getWeekNum(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int posOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        posOfWeek--;
        String[] ary = "7,1,2,3,4,5,6".split(",");
        return Integer.parseInt(ary[posOfWeek]);
    }

    /**
     * 开发人： 王涛
     * 开发时间： 2011-6-23 上午11:06:05
     * 功能描述：获取指定月份的天数
     * 方法的参数和返回值
     *
     * @param year 年份
     * @param mon  月份 取值范围 1-12
     * @return int
     * ==================================
     * 修改历史
     * 修改人        修改时间      修改原因及内容
     * <p>
     * ==================================
     */
    public static int getDays(int year, int mon) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, year);
        ca.set(Calendar.MONTH, mon - 1);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 开发人： 王涛
     * 开发时间： 2011-6-23 上午10:30:41
     * 功能描述：按指定格式对字符串进行日期转换
     * 方法的参数和返回值
     *
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException Date
     *                        ==================================
     *                        修改历史
     *                        修改人        修改时间      修改原因及内容
     *                        <p>
     *                        ==================================
     */
    public static Date parse(String dateStr, String format) {
        try{
            if (dateStr == null){
                return null;
            }
            sdf.applyPattern(format);
            return sdf.parse(dateStr);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

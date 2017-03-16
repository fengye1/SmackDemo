package com.lvpf.samckdemo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern phone = Pattern
            .compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转为日期类型 yyyy-MM-dd HH:mm:ss
     *
     * @param sdate
     * @return yyyy-MM-dd HH:mm:ss类型的日期
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 字符串转日期 yyyy-MM-dd
     *
     * @param sdate
     * @return yyyy-MM-dd
     */
    public static Date toDate2(String sdate) {

        try {
            return dateFormater2.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }

    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(Long sdate) {
        Date time = new Date(sdate);
        // if (StringUtils.isInEasternEightZones()) {
        // time = toDate(sdate);
        // } else {
        // time = StringUtils.transformTime(toDate(sdate),
        // TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        // }
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个合法的手机号
     *
     * @param num
     * @return
     */
    public static boolean isTelNum(String num) {
        if (num == null || num.trim().length() == 0)
            return false;
        return phone.matcher(num).matches();
    }

    /**
     * 判断是不是一个大于6位小于16位的密码
     *
     * @param num
     * @return
     */
    public static boolean isPassword(String num) {
        if (num == null || num.trim().length() == 0)
            return false;
        return (num.length() >= 6) && (num.length() <= 16);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * 当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss的时间
     */
    public static String currentTime() {
        // TODO Auto-generated method stub
        SimpleDateFormat dateFormater = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormater.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 时间
     *
     * @return yyyy-MM-dd HH:mm:ss的时间
     */
    public static String currentTime(long date) {
        // TODO Auto-generated method stub
        SimpleDateFormat dateFormater = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormater.format(new Date(date));
    }

    /**
     * 当前日期
     *
     * @return yyyy-MM-dd 日期
     */
    public static String currentDate() {
        // TODO Auto-generated method stub
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormater.format(new Date(System.currentTimeMillis()));
    }

    public static String currentDate1() {
        // TODO Auto-generated method stub
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        long time = (24 * 60 * 60 * 1000l * 30);
        long longdate = System.currentTimeMillis() + time;
        System.out.println("time===>" + System.currentTimeMillis() + "------"
                + time + "-----" + longdate);
        return dateFormater.format(new Date(longdate));
    }

    public static String getFilePaths(List<File> mFileList) {
        // TODO Auto-generated method stubm
        if (null == mFileList)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mFileList.size(); i++) {
            File file = mFileList.get(i);
            if (null != file)
                sb.append(file.getAbsolutePath() + ",");

        }
        return sb.toString();
    }

    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     *
     * @return
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
            defaultVaule = true;
        else
            defaultVaule = false;
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间 2014年7月31日
     *
     * @param time
     * @return
     */
    public static Date transformTime(Date date, TimeZone oldZone,
                                     TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;

    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 处理空字符串
     *
     * @param str
     * @return String
     */
    public static String doEmpty(String str) {
        return doEmpty(str, "");
    }

    /**
     * 处理空字符串
     *
     * @param str
     * @param defaultValue
     * @return String
     */
    public static String doEmpty(String str, String defaultValue) {
        if (str == null || str.equalsIgnoreCase("null")
                || str.trim().equals("") || str.trim().equals("－请选择－")) {
            str = defaultValue;
        } else if (str.startsWith("null")) {
            str = str.substring(4, str.length());
        }
        return str.trim();
    }

    /**
     * 请选择
     */
    final static String PLEASE_SELECT = "请选择...";

    public static boolean notEmpty(Object o) {
        return o != null && !"".equals(o.toString().trim())
                && !"null".equalsIgnoreCase(o.toString().trim())
                && !"undefined".equalsIgnoreCase(o.toString().trim())
                && !PLEASE_SELECT.equals(o.toString().trim());
    }

    public static boolean empty(Object o) {
        return o == null || "".equals(o.toString().trim())
                || "null".equalsIgnoreCase(o.toString().trim())
                || "undefined".equalsIgnoreCase(o.toString().trim())
                || PLEASE_SELECT.equals(o.toString().trim());
    }

    /**
     * 给JID返回用户名
     *
     * @param Jid
     * @return
     */
    public static String getUserNameByJid(String Jid) {
        if (empty(Jid)) {
            return null;
        }
        if (!Jid.contains("@")) {
            return Jid;
        }
        return Jid.split("@")[0];
    }

    /**
     * 将字符串中间4位转为星
     *
     * @param str
     * @return
     */
    public static String toStarString(String str) {
        String beforeA = str.substring(0, 3);
        String beforeB = str.substring(7);
        String strll = beforeA + "****" + beforeB;

        return strll;

    }

    /**
     * 在字符串中获取数字
     *
     * @param str
     * @return
     */
    public static String getNumbers(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        System.out.println(m.replaceAll("").trim());
        return m.replaceAll("").trim();
    }

    public static boolean isNum(String string) {
        // TODO Auto-generated method stub
        if (string == null) return false;
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 是否是url路径
     * @param str
     * @return
     */
    public static boolean isURl(String str) {
        Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(str);
        return matcher.matches();
    }
}

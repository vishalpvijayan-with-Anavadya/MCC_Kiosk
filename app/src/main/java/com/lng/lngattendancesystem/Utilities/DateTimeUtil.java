package com.lng.lngattendancesystem.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static String getonlydate(String parsedDate) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date getDateObject = df.parse(parsedDate);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(getDateObject);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date getTime(String serverTime) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date getDateObject = df.parse(serverTime);
            return getDateObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date paresdTime(String serverTime) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date getParedTime = parser.parse("10:00");
            return getParedTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getISTLocalTime() {
        try {
            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            //dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            Date date = new Date();
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}


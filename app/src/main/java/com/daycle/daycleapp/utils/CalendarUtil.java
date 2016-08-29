package com.daycle.daycleapp.utils;

import android.support.annotation.Nullable;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by neoam on 2016-08-22.
 */
public class CalendarUtil {

    // 날짜를 string 형식( 2016-10-31) 으로 리턴
    public static String getDayString(int y, int m, int d){
        String year = String.valueOf(y);
        String month = doubleString(m);
        String date = doubleString(d);

        return year + "-" + month + "-" + date;
    }

    // 날짜를 string 형식( 2016-10-31) 으로 리턴
    public static String getDayString(Calendar day){
        String year = String.valueOf(day.get(Calendar.YEAR));
        String month = doubleString(day.get(Calendar.MONTH) + 1);
        String date = doubleString(day.get(Calendar.DAY_OF_MONTH));

        return year + "-" + month + "-" + date;
    }

    /**
     * 숫자를 2자리 문자로 변환, 2 -> 02
     * @param value
     * @return
     */
    public static String doubleString(int value)
    {
        String temp;

        if(value < 10){
            temp = "0"+ String.valueOf(value);

        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }

    // 로케일에 맞는 Weekday 배열 리턴
    public static String[] getWeekdayDisplayNames(boolean isShort, @Nullable Locale locale){
        DateFormatSymbols symbols = new DateFormatSymbols(locale == null ? Locale.getDefault() : locale);
        return isShort ? symbols.getShortWeekdays() : symbols.getWeekdays();
    }

    public static String[] getWeekdayDisplayNames(boolean isShort){
        return getWeekdayDisplayNames(isShort, null);
    }
}

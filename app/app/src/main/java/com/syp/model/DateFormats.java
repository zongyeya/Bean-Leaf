package com.syp.model;

// Data Structure imports
import java.util.Calendar;
import java.util.Date;

// Class that holds functions to get Date Objects as Strings
public class DateFormats {

    // Strings for graph indexes
    final static public String[] weekDays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    final static public String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    // --------------------------------
    // Get String Week Day for specific date
    // --------------------------------
    public static String getWeekDayString(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1];
    }

    // --------------------------------
    // Get String Date for specific date
    // --------------------------------
    public static String getDateString(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DAY_OF_MONTH);
    }
}

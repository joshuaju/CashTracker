package jungen.com.cashtracker.misc;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joshua Jungen on 03.05.2017.
 */

public class DateHelper {

    private final static DateFormat format = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

    public static String format(Calendar calendar){
        return format(calendar.getTime());
    }

    public static String format(Date date){
        return format.format(date);
    }

    public static String format(long timeInMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        return format(cal);
    }

    public static Calendar getStartOfYear(Calendar calendar){
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        return getStartOfDay(calendar);
    }

    public static Calendar getEndOfYear(Calendar calendar){
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        return getEndOfDay(calendar);
    }

    public static Calendar getStartOfMonth(Calendar calendar){
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        return getStartOfDay(calendar);
    }

    public static Calendar getEndOfMonth(Calendar calendar){
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        return getEndOfDay(calendar);
    }

    public static Calendar getStartOfWeek(Calendar calendar){
        int dayOfWeek = calendar.getFirstDayOfWeek() - calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek);
        return getStartOfDay(calendar);
    }

    public static Calendar getEndOFWeek(Calendar calendar){
        calendar = getStartOfWeek(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        return getEndOfDay(calendar);
    }

    public static Calendar getStartOfDay(Calendar calendar){
        int minHour = calendar.getActualMinimum(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, minHour);
        return calendar;
    }

    public static Calendar getEndOfDay(Calendar calendar){
        int maxHour = calendar.getActualMaximum(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, maxHour);
        return calendar;
    }

}


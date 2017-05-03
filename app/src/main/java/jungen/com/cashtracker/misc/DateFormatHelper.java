package jungen.com.cashtracker.misc;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joshua Jungen on 03.05.2017.
 */

public class DateFormatHelper {

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

}


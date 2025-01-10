package com.neos.simulator.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.neos.simulator.util.Utils;

/**
 * Base class for handling date types with random generation within a range.
 */
public abstract class BaseDateType extends TypeHandler {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private Date min;
    private Date max;

    @Override
    public void setArguments(List<Object> launchArguments) {
        try {
            switch (launchArguments.size()) {
                case 0:
                    min = SDF.parse("1970-01-01T00:00:00Z");
                    max = new Date();
                    break;
                case 1:
                    min = SDF.parse(Utils.stripQuotes((String) launchArguments.get(0)));
                    max = new Date();
                    break;
                case 2:
                    min = SDF.parse(Utils.stripQuotes((String) launchArguments.get(0)));
                    max = SDF.parse(Utils.stripQuotes((String) launchArguments.get(1)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid number of arguments");
            }

            if (!min.before(max)) {
                throw new IllegalArgumentException("Min Date must be before Max Date");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Provided date is invalid. Please use the format [ yyyy-MM-dd'T'HH:mm:ss'Z' ]", e);
        }
    }

    public Calendar generateRandomDate() {
        Calendar calendar = Calendar.getInstance();

        // Set year
        calendar.setTime(min);
        int minYear = calendar.get(Calendar.YEAR);
        calendar.setTime(max);
        int maxYear = calendar.get(Calendar.YEAR);
        int year = getRand().nextInt(minYear, maxYear + 1);
        calendar.set(Calendar.YEAR, year);

        // Set month
        int minMonth = (year == minYear) ? min.getMonth() : 0;
        int maxMonth = (year == maxYear) ? max.getMonth() : 11;
        int month = getRand().nextInt(minMonth, maxMonth + 1);
        calendar.set(Calendar.MONTH, month);

        // Set day
        int minDay = (year == minYear && month == minMonth) ? min.getDate() : 1;
        int maxDay = (year == maxYear && month == maxMonth) ? max.getDate() : calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = getRand().nextInt(minDay, maxDay + 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        // Set time
        int minHour = (calendar.getTime().equals(min)) ? min.getHours() : 0;
        int maxHour = (calendar.getTime().equals(max)) ? max.getHours() : 23;
        int hour = getRand().nextInt(minHour, maxHour + 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        int minMinute = (hour == minHour) ? min.getMinutes() : 0;
        int maxMinute = (hour == maxHour) ? max.getMinutes() : 59;
        int minute = getRand().nextInt(minMinute, maxMinute + 1);
        calendar.set(Calendar.MINUTE, minute);

        int minSecond = (minute == minMinute) ? min.getSeconds() : 0;
        int maxSecond = (minute == maxMinute) ? max.getSeconds() : 59;
        int second = getRand().nextInt(minSecond, maxSecond + 1);
        calendar.set(Calendar.SECOND, second);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
    
    public String getRandomDate() {
        Calendar cal = generateRandomDate();
        return SDF.format(cal.getTime());
    }

    public String getCurrentDate() {
        return SDF.format(new Date());
    }
    
    public long getRandomTimestamp() {
        Calendar cal = generateRandomDate();
        return cal.getTimeInMillis();
    }
    
    public long getCurrentTimestamp() {
        long currentMillis = System.currentTimeMillis();
        return currentMillis;
    }

}

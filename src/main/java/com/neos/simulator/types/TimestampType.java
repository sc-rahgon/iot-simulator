package com.neos.simulator.types;

import com.neos.simulator.Main;
import com.neos.simulator.cache.SimpleCacheManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimestampType extends BaseDateType {
    public static final String TYPE_NAME = "timestamp";
    public static final String TYPE_DISPLAY_NAME = "Timestamp";

    private boolean isNow;
    private int numberOfArguments;
    List<Object> getAllArguments;

    public TimestampType() {
    }

    @Override
    public void setArguments(List<Object> arguments) {
        if (arguments.size() == 0) {
            isNow = true;
        } else {
            isNow = false;
        }
        numberOfArguments = arguments.size();
        getAllArguments = arguments;
    }

    @Override
    public Long getValue() {
        if (isNow) {
            return getCurrentTimestamp();
        } else if (!isNow && numberOfArguments == 3) {
            return generateIntervalTimestamp();
        } else {
            return getRandomTimestamp();
        }
    }

    private Long generateIntervalTimestamp() {
        List<Object> args = getAllArguments;
        SimpleCacheManager<String, Object> cacheManager = Main.RequestProcessorData.cache;
        Date startDate = (Date) args.get(0);
        Date endDate = (Date) args.get(1);
        int interval = (int) args.get(2);
        if (startDate != null && endDate != null) {
            if (cacheManager.containsKey(startDate.toString())) {
                Date updatedStartDate = (Date) cacheManager.get(startDate.toString());
                if(!updatedStartDate.after(endDate)) {
                    cacheManager.remove(startDate.toString());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(updatedStartDate);
                    calendar.add(Calendar.SECOND, interval);
                    cacheManager.put(startDate.toString(), calendar.getTime());
                    return calendar.getTimeInMillis();
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.MILLISECOND, interval);
                cacheManager.put(startDate.toString(), calendar.getTime());
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}

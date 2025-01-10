package com.neos.simulator.types;

import java.util.List;

public class DateType extends BaseDateType {
    public static final String TYPE_NAME = "date";
    public static final String TYPE_DISPLAY_NAME = "Date";
    
    private boolean isNow;

    public DateType() {
    }
    
    @Override
    public void setArguments(List<Object> arguments) {
        if (arguments.size() == 0) {
        	isNow = true;
        }else {
        	isNow = false;
        }
    }

    
    @Override
    public String getValue() {
    	if (isNow) {
    		return getCurrentDate();
    	}
        return getRandomDate();
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }
}

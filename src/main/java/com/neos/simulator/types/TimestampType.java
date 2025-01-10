package com.neos.simulator.types;

import java.util.List;

public class TimestampType extends BaseDateType {
    public static final String TYPE_NAME = "timestamp";
    public static final String TYPE_DISPLAY_NAME = "Timestamp";

    private boolean isNow;
    
    public TimestampType() {
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
    public Long getValue() {
    	if (isNow) {
    		return getCurrentTimestamp();
    	}
        return getRandomTimestamp();
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }
}

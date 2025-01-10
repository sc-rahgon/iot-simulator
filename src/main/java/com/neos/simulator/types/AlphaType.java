package com.neos.simulator.types;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

public class AlphaType extends TypeHandler {
    public static final String TYPE_NAME = "alpha";
    public static final String TYPE_DISPLAY_NAME = "Alphabetic";
    
    private int length;

    public AlphaType() {
    }
    
    @Override
    public void setArguments(List<Object> launchArguments) {
        super.setArguments(launchArguments);
        if (launchArguments.size() != 1) {
            throw new IllegalArgumentException("You must specifc a length for Alpha Numeric types");
        }
        length = (int) launchArguments.get(0);
    }

    
    @Override
    public String getValue() {
        return RandomStringUtils.randomAlphabetic(length);
    }
            
    @Override
    public String getName() {
        return TYPE_NAME;
    }
}

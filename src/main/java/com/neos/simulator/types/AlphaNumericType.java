package com.neos.simulator.types;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

public class AlphaNumericType extends TypeHandler {
    public static final String NAME = "alphaNumeric";
    public static final String DISPLAY_NAME = "Alpha Numeric";
    
    private int length;

    public AlphaNumericType() {
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
        return RandomStringUtils.randomAlphanumeric(length);
    }

    @Override
    public String getName() {
        return NAME;
    }
            
}

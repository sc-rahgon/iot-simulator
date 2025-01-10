package com.neos.simulator.types;

import java.util.List;

public class ConstantType extends TypeHandler {
    public static final String TYPE_NAME = "constant";
    public static final String TYPE_DISPLAY_NAME = "Constant";
    
    private Object constant;
    
    @Override
    public void setArguments(List<Object> launchArguments) {
        super.setArguments(launchArguments);
        if (launchArguments.size() != 1) {
            throw new IllegalArgumentException("You must specifc a length for Alpha Numeric types");
        }
        constant = launchArguments.get(0);
    }

    @Override
    public Object getValue() {
        return constant;
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }
            
}

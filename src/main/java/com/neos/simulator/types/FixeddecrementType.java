package com.neos.simulator.types;

import java.util.List;

public class FixeddecrementType extends IncrementDecrementType {
    public static final String TYPE_NAME = "fixedDecrement";
    public static final String TYPE_DISPLAY_NAME = "Fixed Decrement";
    
    @Override
    public void setArguments(List<Object> launchArguments) {  
    	super.setArguments(false, true, launchArguments);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    } 
}

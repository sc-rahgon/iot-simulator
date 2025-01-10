package com.neos.simulator.types;

import java.util.List;

public class FixedIncrementType extends IncrementDecrementType {
    public static final String TYPE_NAME = "fixedIncrement";
    public static final String TYPE_DISPLAY_NAME = "fixed Increment";
    
    @Override
    public void setArguments(List<Object> launchArguments) {  
    	super.setArguments(true, true, launchArguments);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    } 
}

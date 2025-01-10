package com.neos.simulator.types;

import java.util.List;

public class RandomIncrementType extends IncrementDecrementType {
    public static final String TYPE_NAME = "randomIncrement";
    public static final String TYPE_DISPLAY_NAME = "Random Increment";
    
    @Override
    public void setArguments(List<Object> launchArguments) {  
    	super.setArguments(true, false, launchArguments);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    } 
}

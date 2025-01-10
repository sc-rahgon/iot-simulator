package com.neos.simulator.types;

import java.util.List;

public class RandomDecrementType extends IncrementDecrementType {
    public static final String TYPE_NAME = "randomDecrement";
    public static final String TYPE_DISPLAY_NAME = "Random Decrement";
    
    @Override
    public void setArguments(List<Object> launchArguments) {  
    	super.setArguments(false, false, launchArguments);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    } 
}

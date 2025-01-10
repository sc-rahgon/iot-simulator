package com.neos.simulator.types;

import java.util.List;

public class RandomSelectionType extends TypeHandler {

    public static final String TYPE_NAME = "randomSelection";
    public static final String TYPE_DISPLAY_NAME = "Random Selection";

    private List<Object> values;

    @Override
    public void setArguments(List<Object> launchArguments) {
    	 super.setArguments(launchArguments);
         if (launchArguments.size() != 1) {
             throw new IllegalArgumentException("You must specifc a list of values");
         }
         values = (List<Object>) launchArguments.get(0);
    }

    @Override
    public Object getValue() {
        return values.get(getRand().nextInt(0, values.size() - 1));
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }    
}

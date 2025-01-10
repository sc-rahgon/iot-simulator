package com.neos.simulator.types;

import java.util.List;

public class LongType extends TypeHandler {

    public static final String TYPE_NAME = "long";
    public static final String TYPE_DISPLAY_NAME = "Long";

    private long min;
    private long max;

    public LongType() {
    }
    
    @Override
    public void setArguments(List<Object> launchArguments) {
        super.setArguments(launchArguments);
        if (launchArguments.size() == 0) {
            min = 0;
            max = Long.MAX_VALUE;
        } else if (launchArguments.size() == 1) {
            min = (long) launchArguments.get(0);
            max = Long.MAX_VALUE;
        } else if (launchArguments.size() == 2) {
            min = (long) launchArguments.get(0);
            max = (long) launchArguments.get(1);
        }
    }

    @Override
    public Long getValue() {
        return getRand().nextLong(min, max);
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }

}

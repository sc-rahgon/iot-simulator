package com.neos.simulator.types;

import java.util.List;

public class IntegerType extends TypeHandler {

    public static final String TYPE_NAME = "integer";
    public static final String TYPE_DISPLAY_NAME = "Integer";

    private int min;
    private int max;

    @Override
    public void setArguments(List<Object> launchArguments) {
        super.setArguments(launchArguments);
        if (launchArguments.size() == 0) {
            min = 0;
            max = Integer.MAX_VALUE;
        } else if (launchArguments.size() == 1) {
            min = (int) launchArguments.get(0);
            max = Integer.MAX_VALUE;
        } else if (launchArguments.size() == 2) {
            min = (int) launchArguments.get(0);
            max = (int) launchArguments.get(1);
        }
    }

    @Override
    public Integer getValue() {
        return getRand().nextInt(min, max);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}

package com.neos.simulator.types;

import java.util.List;
import java.util.Random;
import org.apache.commons.math3.util.Precision;

public class DoubleType extends TypeHandler {

    public static final String TYPE_NAME = "double";
    public static final String TYPE_DISPLAY_NAME = "Double";

    private double min;
    private double max;
    private Random rand;
    private static final int decimalPlaces = 4;

    public DoubleType() {
        super();
        rand = new Random();
    }

    @Override
    public void setArguments(List<Object> launchArguments) {
        super.setArguments(launchArguments);
        if (launchArguments.size() == 0) {
            min = 0;
            max = Double.MAX_VALUE;
        } else if (launchArguments.size() == 1) {
            min = (double) launchArguments.get(0);
            max = Double.MAX_VALUE;
        } else if (launchArguments.size() == 2) {
            min = (double) launchArguments.get(0);
            max = (double) launchArguments.get(1);
        }
    }

    @Override
    public Double getValue() {
        double range = max - min;
        double scaled = rand.nextDouble() * range;
        double shifted = scaled + min;

        return Precision.round(shifted, decimalPlaces);

    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

}

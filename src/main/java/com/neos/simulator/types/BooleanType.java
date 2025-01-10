package com.neos.simulator.types;

import java.util.Random;

public class BooleanType extends TypeHandler {
    public static final String TYPE_NAME = "boolean";
    public static final String TYPE_DISPLAY_NAME = "Boolean";

    @Override
    public Boolean getValue() {
        return new Random().nextBoolean();
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }
            
}

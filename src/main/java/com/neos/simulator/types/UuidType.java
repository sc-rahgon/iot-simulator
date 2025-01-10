package com.neos.simulator.types;

import java.util.UUID;

public class UuidType extends TypeHandler {
    public static final String TYPE_NAME = "uuid";
    public static final String TYPE_DISPLAY_NAME = "UUID";

    @Override
    public String getValue() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public String getName() {
        return TYPE_NAME;
    }
            
}

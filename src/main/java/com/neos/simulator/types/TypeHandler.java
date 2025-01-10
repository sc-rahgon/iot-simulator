package com.neos.simulator.types;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;


public abstract class TypeHandler {
    private RandomDataGenerator rand;
    private List<Object> arguments;
    
    public TypeHandler() {
        rand = new RandomDataGenerator();
    }
    
    public abstract Object getValue();
    public abstract String getName();
    

    public RandomDataGenerator getRand() {
        return rand;
    }

    public void setRand(RandomDataGenerator rand) {
        this.rand = rand;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> args) {
        this.arguments = args;
    }
}

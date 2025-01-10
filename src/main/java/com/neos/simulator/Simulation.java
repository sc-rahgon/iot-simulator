package com.neos.simulator;

import java.util.Map;

public class Simulation {
    private long frequency = 10000;
    private String type = "random";
    private long iterations = -1;
    private DeviceSetting device;
    private DeviceSetting gateway;
    private Map<String, Object> attributes;

    public Simulation() {
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getIterations() {
        return iterations;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    public DeviceSetting getDevice() {
        return device;
    }

    public void setDevice(DeviceSetting device) {
        this.device = device;
    }

    public DeviceSetting getGateway() {
        return gateway;
    }

    public void setGateway(DeviceSetting gateway) {
        this.gateway = gateway;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}

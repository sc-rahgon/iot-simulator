package com.neos.simulator;

public class DeviceSetting {
    private long count = 1;
    private String prefix = "";

    public DeviceSetting() {
    }

    public DeviceSetting(long count, String prefix) {
        this.count = count;
        this.prefix = prefix;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be non-negative.");
        }
        this.count = count;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "DeviceSetting{" +
               "count=" + count +
               ", prefix='" + prefix + '\'' +
               '}';
    }
}

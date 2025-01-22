package com.neos.simulator.dto;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

public class CreateSimulationRequestDTO {
    private String profileID;
    private String protocol;
    private long numberOfDevices;
    private long deviceIdIncrement;
    private HashMap<String, Object> attributes;
    private int simulationFrequency;

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getNumberOfDevices() {
        return numberOfDevices;
    }

    public void setNumberOfDevices(long numberOfDevices) {
        this.numberOfDevices = numberOfDevices;
    }

    public long getDeviceIdIncrement() {
        return deviceIdIncrement;
    }

    public void setDeviceIdIncrement(long deviceIdIncrement) {
        this.deviceIdIncrement = deviceIdIncrement;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public int getSimulationFrequency() {
        return simulationFrequency;
    }

    public void setSimulationFrequency(int simulationFrequency) {
        this.simulationFrequency = simulationFrequency;
    }

    public boolean isDurationDefined() {
        return isDurationDefined;
    }

    public void setDurationDefined(boolean durationDefined) {
        isDurationDefined = durationDefined;
    }

    public List<OffsetDateTime> getDates() {
        return dates;
    }

    public void setDates(List<OffsetDateTime> dates) {
        this.dates = dates;
    }

    private boolean isDurationDefined;
    private List<OffsetDateTime> dates;
}

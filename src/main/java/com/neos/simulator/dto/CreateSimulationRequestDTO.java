package com.neos.simulator.dto;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

public class CreateSimulationRequestDTO {
    private String simulationName;
    private String profileID;
    private String protocol;
    private long numberOfDevices;
    private String devicePrefix;
    private long deviceIdIncrement;
    private HashMap<String, Object> attributes;
    private long simulationFrequency;
    private String isDurationDefined;
    private List<OffsetDateTime> dates;
    private String emailId;

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

    public long getSimulationFrequency() {
        return simulationFrequency;
    }

    public void setSimulationFrequency(long simulationFrequency) {
        this.simulationFrequency = simulationFrequency;
    }

    public String isDurationDefined() {
        return isDurationDefined;
    }

    public void setDurationDefined(String durationDefined) {
        isDurationDefined = durationDefined;
    }

    public List<OffsetDateTime> getDates() {
        return dates;
    }

    public void setDates(List<OffsetDateTime> dates) {
        this.dates = dates;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDevicePrefix() {
        return devicePrefix;
    }

    public void setDevicePrefix(String devicePrefix) {
        this.devicePrefix = devicePrefix;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }
}

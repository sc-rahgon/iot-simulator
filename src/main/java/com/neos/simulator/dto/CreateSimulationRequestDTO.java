package com.neos.simulator.dto;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateSimulationRequestDTO {
    private String simulationName;
    private String profileID;
    private String protocol;
    private HashMap<String, Object> attributes;
    private HashMap<String, String> device;
    private HashMap<String, String> gateway;
    private long simulationFrequency;
    private String durationDefine;
    private List<Date> dates;
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

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }


    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getDurationDefine() {
        return durationDefine;
    }

    public void setDurationDefine(String durationDefine) {
        this.durationDefine = durationDefine;
    }

    public HashMap<String, String> getDevice() {
        return device;
    }

    public void setDevice(HashMap<String, String> device) {
        this.device = device;
    }

    public HashMap<String, String> getGateway() {
        return gateway;
    }

    public void setGateway(HashMap<String, String> gateway) {
        this.gateway = gateway;
    }
}

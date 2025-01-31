package com.neos.simulator.dto;

public class StopSimulationRequestDTO {
    private String email;
    private String simulationName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }
}

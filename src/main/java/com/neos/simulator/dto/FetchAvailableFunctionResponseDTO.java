package com.neos.simulator.dto;

public class FetchAvailableFunctionResponseDTO {
    private String function;
    private String description;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.neos.simulator.dto;

public class AttributeRequestDTO {
    private String upperLimit;
    private String lowerLimit;
    private String typeOfIncrement;
    private String dataType;
    private String unit;

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getTypeOfIncrement() {
        return typeOfIncrement;
    }

    public void setTypeOfIncrement(String typeOfIncrement) {
        this.typeOfIncrement = typeOfIncrement;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

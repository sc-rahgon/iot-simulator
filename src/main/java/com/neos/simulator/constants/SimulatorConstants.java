package com.neos.simulator.constants;

import java.util.*;

public class SimulatorConstants {
    public static String parameterConfig = "{type}({lowerLimit}, {upperLimit}, {frequency})";
    public static String INSERT_INTO_SIMULATION_DETAILS = "INSERT INTO SIMULATION_DETAILS (EMAIL, THREAD_DETAILS, TIMESTAMP, SIMULATION_NAME, SIMULATION_UUID, IS_ACTIVE) VALUES (?, ?, ?,?,?)";
    public static HashMap<String, String> FUNCTIONS = new LinkedHashMap<>();
    static {
        FUNCTIONS.put("timestamp()", "generates a random timestamp");
        FUNCTIONS.put("timestamp(startDate, endDate, frequency)", "generates a timestamp between start date and end date and difference between the previous and the new timestamp is frequency.");
        FUNCTIONS.put("alpha(parameter)", "generate random alphabetical string based on size defined as parameter");
        FUNCTIONS.put("boolean()", "generate random boolean based on size defined as parameter");
        FUNCTIONS.put("date()", "generate random date based on size defined as parameter");
        FUNCTIONS.put("double()", "generate random double based on size defined as parameter");
        FUNCTIONS.put("double(lowerLimit, upperLimit)", "generates a random double value in between upper limit and lower limit");
        FUNCTIONS.put("fixedIncrement(lowerLimit, incrementValue, upperLimit)", "generates value increment based on lowerLimit + fixedIncrementValue example 0,0.1,10 - generates value in increasing order of 0,0.1,0.2 etc");
        FUNCTIONS.put("fixedDecrement(lowerLimit, incrementValue, upperLimit)", "generates a random integer value in between upper and lower limit");
        FUNCTIONS.put("integer()", "generate random integer based on size defined as parameter");
        FUNCTIONS.put("constant(value)", "constant value is returned");
        FUNCTIONS.put("long(lowerLimit, upperLimit)", "generate a random long value between lowerLimit and upperLimit");
        FUNCTIONS.put("long()", "generate a random long value between lowerLimit and upperLimit");
        FUNCTIONS.put("randomIncrement(base, lowerLimit, upperLimit)", "generates random increment value");
        FUNCTIONS.put("randomDecrement(base, lowerLimit, upperLimit)", "generates random decrement value");
        FUNCTIONS.put("alphaNumeric(10)", "generates a random alpha numeric string based of length");
        FUNCTIONS.put("uuid()", "generates a random UUID string based on size defined as parameter");
    }
}

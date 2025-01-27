package com.neos.simulator.constants;

import java.util.*;

public class SimulatorConstants {
    public static String parameterConfig = "{type}({lowerLimit}, {upperLimit}, {frequency})";
    public static String INSERT_INTO_SIMULATION_DETAILS = "INSERT INTO SIMULATION_DETAILS (EMAIL, THREAD_DETAILS, TIMESTAMP, SIMULATION_NAME, SIMULATION_UUID, IS_ACTIVE) VALUES (?, ?, ?,?,?)";
    public static List<String> FUNCTIONS = Arrays.asList("timestamp()",
            "alpha(parameter)",
            "boolean()",
            "date()",
            "double()",
            "doule(lowerLimit, upperLimit)",
            "fixedIncrement(lowerLimit, incrementValue, upperLimit)",
            "fixedDecrement(lowerLimit, incrementValue, upperLimit)",
            "integer(lowerLimit, upperLimit)",
            "integer()",
            "timestamp()",
            "timestamp(startDate, endDate, frequency)",
            "long(lowerLimit, upperLimit)",
            "long()",
            "randomIncrement(base, lowerLimit, upperLimit)",
            "randomDecrement(base, lowerLimit, upperLimit)",
            "alphaNumeric(10)",
            "uuid()");
}

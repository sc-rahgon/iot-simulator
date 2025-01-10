package com.neos.simulator.util;

public class Utils {
	
    public static String stripQuotes(String s) {
        return s.replaceAll("'", "").replaceAll("\"", "").trim();
    }

}

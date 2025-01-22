package com.neos.simulator.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	
    public static String stripQuotes(String s) {
        return s.replaceAll("'", "").replaceAll("\"", "").trim();
    }

    public static Thread findThreadById(long threadId) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getId() == threadId)
                .findFirst()
                .orElse(null);
    }

    public static Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
            params.put(key, value);
        }
        return params;
    }
}

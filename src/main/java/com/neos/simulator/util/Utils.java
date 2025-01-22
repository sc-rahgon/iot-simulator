package com.neos.simulator.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static UUID generateUUID(String input) throws NoSuchAlgorithmException {
        // Hash the input to ensure a consistent length
        byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(StandardCharsets.UTF_8));

        // Use the first 16 bytes to create a UUID
        long mostSignificantBits = 0;
        long leastSignificantBits = 0;

        for (int i = 0; i < 8; i++) {
            mostSignificantBits = (mostSignificantBits << 8) | (hash[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            leastSignificantBits = (leastSignificantBits << 8) | (hash[i] & 0xff);
        }

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}

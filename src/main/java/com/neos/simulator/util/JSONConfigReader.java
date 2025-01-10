package com.neos.simulator.util;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

public class JSONConfigReader {    
    public static <T> T readConfig(String fileName, Class<T> targetClass) throws IOException {
    	Gson gson = new Gson();
        try (FileReader reader = new FileReader(fileName)) {
			return gson.fromJson(reader, targetClass);
		}
    }
}

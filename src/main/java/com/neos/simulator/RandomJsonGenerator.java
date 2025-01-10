/*
* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neos.simulator;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neos.simulator.config.SimulationConfig;
import com.neos.simulator.types.TypeHandler;
import com.neos.simulator.types.TypeHandlerFactory;
import com.neos.simulator.util.JsonUtils;

/**
 *
 * @author andrewserff
 */
public class RandomJsonGenerator {

    private static final Logger log = LogManager.getLogger(RandomJsonGenerator.class);
    private SimpleDateFormat iso8601DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Map<String, Object> config;
    private static JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
    private Map<String, Object> generatedValues;
    private JsonUtils jsonUtils;
    private SimulationConfig simulationConfig;
    private TypeHandlerFactory typeHandlerFactory;

    public RandomJsonGenerator(Map<String, Object> config, SimulationConfig simulationConfig) {
        this.config = config;
        this.simulationConfig = simulationConfig;
        
        jsonUtils = new JsonUtils();
        
        typeHandlerFactory = new TypeHandlerFactory();
        typeHandlerFactory.configure(simulationConfig);
    }

    public Map<String, Object> generateData() {
        Map<String, Object> data = processProperties(config);
        return data;
    }
    
    public String generateJson() {
        Map<String, Object> data = processProperties(config);
        String payload = new Gson().toJson(data);
        System.out.println(payload);
        return payload;
    }

    public String generateFlattnedJson() throws IOException {
        String json = generateJson();
        return jsonUtils.flattenJson(json);
    }

    @SuppressWarnings("unchecked")
	public Map<String, Object> generateJsonMap() throws IOException {
        String json = generateJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map.class);
    }

    public List<Map<String, Object>> generateJsonList() throws IOException {
        String json = generateJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, List.class);
    }

    private void processProperties(JsonObject payload, Map<String, Object> props, String currentContext) {
        for (String propName : props.keySet()) {
            Object value = props.get(propName);
            if (value == null) {
            	payload.add(propName, null);
            } else if (String.class.isAssignableFrom(value.getClass())) {
                String type = (String) value;                
                Object val = handleStringGeneration(type);
                Gson gson = new Gson();
                payload.add(propName, gson.toJsonTree(val));
            } else if (Map.class.isAssignableFrom(value.getClass())) {
                Map<String, Object> nestedProps = (Map<String, Object>) value;
                String newContext = "";
                if (propName != null) {
                    if (currentContext.isEmpty()) {
                        newContext = propName + ".";
                    } else {
                        newContext = currentContext + propName + ".";
                    }
                }
                processProperties(payload, nestedProps, newContext);
            } 
        }
    }

    private Map<String, Object> processProperties(Map<String, Object> props) {
    	Map<String, Object> payload = new HashMap<>();
        for (String propName : props.keySet()) {
            Object value = props.get(propName);
            if (value == null) {
            	payload.put(propName, null);
            } else if (String.class.isAssignableFrom(value.getClass())) {
                String type = (String) value;                
                Object val = handleStringGeneration(type);
                payload.put(propName, val);
            } else if (Map.class.isAssignableFrom(value.getClass())) {
                Map<String, Object> nestedProps = (Map<String, Object>) value;
                payload.put(propName, processProperties(nestedProps));
            } 
        }
        return payload;
    }

    
    protected Object handleStringGeneration(String type) {
    	try {
            TypeHandler th = typeHandlerFactory.getTypeHandler(type);

            if (th != null) {
                Object val = th.getValue();
//                        outputValues.put(propName, val);
//                generatedValues.put(currentContext + propName, val);
//                addValue(gen, propName, val);
                return val;
            } else {
                        log.debug("Unknown Type: [ " + type + " ] f. Attempting to echo literal value.");
            }
        } catch (IllegalArgumentException iae) {
            log.warn("Error creating type [ " + type + " ].  being ignored in output.  Reason: " + iae.getMessage());
            log.debug("Error creating type [ " + type + " ].  being ignored in output.", iae);
        }
    	return null;
    }


}

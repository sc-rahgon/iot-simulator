package com.neos.simulator.util;

import com.google.gson.Gson;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.config.Config;
import com.neos.simulator.controllers.CreateDeviceSimulationEvent;
import com.neos.simulator.producer.*;
import com.neos.simulator.request.EventBuffer;
import com.neos.simulator.request.HttpRequestProcessor;
import com.neos.simulator.request.RequestProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationUtil {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationUtil.class);

    public static HashMap<String, Object> fetchConfigurations() {
        try {
            Config config = getConfig();
            List<EventProducer> producers = new ArrayList<>();
            for (Map<String, Object> producer : config.getProducers()) {
                String type = (String) producer.get("type");
                switch (type) {
                    case "logger": {
                        LOGGER.info("Adding Log4JLogger Producer");
                        producers.add(new Log4JProducer());
                        break;
                    }
                    case "file": {
                        LOGGER.info("Adding File Logger with properties: " + producer);
                        producers.add(new FileProducer(producer));
                        break;
                    }
                    case "kafka": {
                        LOGGER.info("Adding Kafka Producer with properties: " + producer);
                        producers.add(new KafkaMsgProducer(producer));
                        break;
                    }

                    case "httpPost": {
                        LOGGER.info("Adding HTTP Post Logger with properties: " + producer);
                        try {
                            producers.add(new HttpPostProducer(producer));
                        } catch (NoSuchAlgorithmException ex) {
                            LOGGER.error("http-post Logger unable to initialize", ex);
                        }
                        break;
                    }
                    case "mqtt": {
                        LOGGER.info("Adding MQTT Logger with properties: " + producer);
                        try {
                            producers.add(new MqttProducer(producer));
                        } catch (MqttException ex) {
                            LOGGER.error("mqtt Logger unable to initialize", ex);
                        }
                        break;
                    }

                }
            }
			if (producers.isEmpty()) {
				throw new IllegalArgumentException("You must configure at least one Producer in the Simulation Config");
			}

            HashMap<String, Object> response = new HashMap<>();
            response.put("producers", producers);
            response.put("config", config);
            return response;

        } catch (IOException ex) {
            LOGGER.error("Error getting Simulation Config [ " + "defaultConfig.json" + " ]", ex);
        }
        return null;
    }

    private static Config getConfig() throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("defaultConfig.json")) {
            return gson.fromJson(reader, Config.class);
        }
    }
}

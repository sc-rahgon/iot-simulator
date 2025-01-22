package com.neos.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.neos.simulator.cache.SimpleCacheManager;
import com.neos.simulator.config.H2InMemoryDB;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pulsar.client.api.PulsarClientException;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import com.neos.simulator.config.SimulationConfig;
import com.google.gson.Gson;
import com.neos.simulator.config.Config;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.producer.FileProducer;
import com.neos.simulator.producer.HttpPostProducer;
import com.neos.simulator.producer.KafkaMsgProducer;
import com.neos.simulator.producer.Log4JProducer;
import com.neos.simulator.producer.MqttProducer;
import com.neos.simulator.request.EventBuffer;
import com.neos.simulator.request.HttpRequestProcessor;
import com.neos.simulator.request.RequestProcessor;
import com.neos.simulator.util.JSONConfigReader;

public class Main {
	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	private SimulationRunner runner;
	private static String config;
	private EventBuffer buffer;

	public Main() {
		buffer = new EventBuffer();
	}

	public String getFilePath(String file) {
		String filePath = getSimulationContentPath() + "/" + file;
		return filePath;
	}

	public static String getSimulationContentPath() {
		String folder = "/home/anish_agrawal/Desktop/IoT-Sense/iot-simulator/conf";
		return folder;
	}



	public static class RequestProcessorData {
		public static List<RequestProcessor> requestProcessors = new ArrayList<>();
		public static List<EventProducer> eventProducers = new ArrayList<>();
		public static Config config;
		public static SimpleCacheManager<String, Object> cache = new SimpleCacheManager<>(5000, 20000000);
        static {
            try {
                config = getConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
		public static String simulationPath = getSimulationContentPath();
		public static Connection connection = H2InMemoryDB.makeConnection();
    }


	public Main setUpSimulation() {
		try {
			for (Map<String, Object> producer : RequestProcessorData.config.getProducers()) {
				String type = (String) producer.get("type");
				switch (type) {
				case "logger": {
					LOGGER.info("Adding Log4JLogger Producer");
					RequestProcessorData.eventProducers.add(new Log4JProducer());
					break;
				}
				case "file": {
					LOGGER.info("Adding File Logger with properties: " + producer);
					RequestProcessorData.eventProducers.add(new FileProducer(producer));
					break;
				}
				case "kafka": {
					LOGGER.info("Adding Kafka Producer with properties: " + producer);
					RequestProcessorData.eventProducers.add(new KafkaMsgProducer(producer));
					break;
				}
				
				case "httpPost": {
					LOGGER.info("Adding HTTP Post Logger with properties: " + producer);
					try {
						RequestProcessorData.eventProducers.add(new HttpPostProducer(producer));
					} catch (NoSuchAlgorithmException ex) {
						LOGGER.error("http-post Logger unable to initialize", ex);
					}
					break;
				}
				case "mqtt": {
					LOGGER.info("Adding MQTT Logger with properties: " + producer);
					try {
						RequestProcessorData.eventProducers.add(new MqttProducer(producer));
					} catch (MqttException ex) {
						LOGGER.error("mqtt Logger unable to initialize", ex);
					}
					break;
				}
			
				}
			}
			if (RequestProcessorData.eventProducers.isEmpty()) {
				throw new IllegalArgumentException("You must configure at least one Producer in the Simulation Config");
			}
			
			for (Map<String, Object> processor : RequestProcessorData.config.getRequestProcessors()) {
				String type = (String) processor.get("type");
				switch (type) {
				case "httpRequest": {
					LOGGER.info("Adding HTTP request Logger with properties: " + processor);
					try {
						RequestProcessorData.requestProcessors.add(new HttpRequestProcessor(buffer));
					} catch (Exception ex) {
						LOGGER.error("http-request processor", ex);
					}
					break;
				}
			
				}
			}
		} catch (IOException ex) {
			LOGGER.error("Error getting Simulation Config [ " + config + " ]", ex);
		}
		return this;
	}

	

	public static Config getConfig() throws IOException {
		Gson gson = new Gson();
		try (FileReader reader = new FileReader(config)) {
			Config config = gson.fromJson(reader, Config.class);
			return config;
		}
	}


	public String getSimConfigFile() {
		return config;
	}

	public void setConfigFile(String config) {
		this.config = config;
	}

	public SimulationRunner getSimRunner() {
		return runner;
	}
	
//	public void startRunning() {
//		runner.startSimulation();
//	}

	public void stopRunning() {
		runner.stopSimulation();
	}

	public boolean isRunning() {
		return runner.isRunning();
	}


	public static void main(String[] args) {
		String config = "defaultConfig.json";
		if (args.length > 0) {
			config = args[0];
			LOGGER.info("Overriding Simulation Config file from command line to use [ " + config + " ]");
		}

		Main gen = new Main();
		gen.setConfigFile(config);
		gen.setUpSimulation();
	}

}

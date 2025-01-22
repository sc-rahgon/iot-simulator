package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neos.simulator.Main;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.config.Config;
import com.neos.simulator.dto.CreateSimulationRequestDTO;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.request.EventBuffer;
import com.neos.simulator.request.RequestProcessor;
import com.neos.simulator.util.ConfigurationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pulsar.client.impl.Hash;
import org.apache.zookeeper.Op;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.neos.simulator.util.ConfigurationUtil.fetchConfigurations;

public class CreateDeviceSimulationEvent implements HttpHandler {
    private SimulationRunner runner;

    private static final Logger LOGGER = LogManager.getLogger(CreateDeviceSimulationEvent.class);
    public String getSimulationContentPath() {
        String folder = "/home/anish_agrawal/Desktop/IoT-Sense/iot-simulator/conf";
        return folder;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {
            InputStream inputStream = httpExchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            ObjectMapper objectMapper = new ObjectMapper();
            CreateSimulationRequestDTO createSimulationRequestDTO = null;
//            try {
//                createSimulationRequestDTO = objectMapper.readValue(requestBody, CreateSimulationRequestDTO.class);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("UNABLE to process exception");
//            }
            HashMap<String, Object> configurations = fetchConfigurations();
            Config config = null;
            List<EventProducer> producers = new ArrayList<>();
            if(configurations.containsKey("config")) {
                config = (Config) configurations.get("config");
            }
            if(configurations.containsKey("producers")) {
                producers = (List<EventProducer>) configurations.get("producers");
            }
            runner = new SimulationRunner(config, producers, Main.RequestProcessorData.requestProcessors, getSimulationContentPath(), new EventBuffer());

            runner.startSimulation();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }
}

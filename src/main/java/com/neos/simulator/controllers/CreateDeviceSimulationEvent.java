package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neos.simulator.DeviceSetting;
import com.neos.simulator.Main;
import com.neos.simulator.Simulation;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.config.Config;
import com.neos.simulator.config.SimulationConfig;
import com.neos.simulator.dto.AttributeRequestDTO;
import com.neos.simulator.dto.CreateSimulationRequestDTO;
import com.neos.simulator.request.EventBuffer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.neos.simulator.constants.SimulatorConstants.parameterConfig;


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
            try {
                createSimulationRequestDTO = objectMapper.readValue(requestBody, CreateSimulationRequestDTO.class);
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getStackTrace());
                throw new RuntimeException("UNABLE to process exception");
            }

            Simulation simulation = setSimulationConfig(createSimulationRequestDTO);
            runner = new SimulationRunner(Main.RequestProcessorData.config, Main.RequestProcessorData.eventProducers, Main.RequestProcessorData.requestProcessors, Main.RequestProcessorData.simulationPath, new EventBuffer(), simulation);

            runner.startSimulation(createSimulationRequestDTO.getEmailId(), createSimulationRequestDTO.getSimulationName());
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }

    private Simulation setSimulationConfig(CreateSimulationRequestDTO createSimulationRequestDTO) {
        Simulation simulation = new Simulation();
        DeviceSetting deviceSetting = new DeviceSetting();
        deviceSetting.setPrefix(createSimulationRequestDTO.getDevicePrefix());
        deviceSetting.setCount(createSimulationRequestDTO.getNumberOfDevices());
        simulation.setDevice(deviceSetting);
        Map<String, Object> requestBodyAttribute = createSimulationRequestDTO.getAttributes();
        Map<String, Object> attributes = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Map.Entry<String, Object> entry : requestBodyAttribute.entrySet()) {
            AttributeRequestDTO attributeRequestDTO = new AttributeRequestDTO();
            try {
                attributeRequestDTO = objectMapper.readValue(objectMapper.writeValueAsString(entry.getValue()), AttributeRequestDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(Objects.nonNull(attributeRequestDTO)) {
                String attributeFormat = parameterConfig
                        .replace("{type}", attributeRequestDTO.getDataType())
                        .replace("{lowerlimit}", attributeRequestDTO.getLowerLimit())
                        .replace("{upperLimit}", attributeRequestDTO.getUpperLimit());
                attributes.put(entry.getKey(), attributeFormat);
            }
        }
        simulation.setAttributes(attributes);
        simulation.setFrequency(createSimulationRequestDTO.getSimulationFrequency());
        simulation.setGateway(new DeviceSetting(10, "G"));
        simulation.setType("batch");
        return simulation;
    }
}

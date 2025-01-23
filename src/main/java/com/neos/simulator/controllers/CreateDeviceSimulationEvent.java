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
import com.neos.simulator.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            String threadIds = runner.startSimulation();
            saveDetailsToDatabase(threadIds, createSimulationRequestDTO);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }

    private void saveDetailsToDatabase(String threadIds, CreateSimulationRequestDTO createSimulationRequestDTO) {
        try {
            String sessionUUID = String.valueOf(Utils.generateUUID(createSimulationRequestDTO.getEmailId() + createSimulationRequestDTO.getSimulationName()));
            String createStatement = "CREATE TABLE IF NOT EXISTS SIMULATION_DETAILS(\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    email VARCHAR(255),\n" +
                    "    thread_details VARCHAR(255),\n" +
                    "    timestamp DATETIME,\n" +
                    "    simulation_name VARCHAR(255),\n" +
                    "    session_uuid VARCHAR(255),\n" +
                    "    is_active BOOLEAN DEFAULT TRUE\n" +
                    ");\n";
            Main.RequestProcessorData.connection.createStatement().execute(createStatement);
            PreparedStatement statement = Main.RequestProcessorData.connection.prepareStatement("INSERT INTO SIMULATION_DETAILS (EMAIL, THREAD_DETAILS, TIMESTAMP, SIMULATION_NAME, SESSION_UUID, IS_ACTIVE) VALUES (?, ?, ?,?,?, ?)");
            statement.setString(1, createSimulationRequestDTO.getEmailId());
            statement.setString(2, threadIds);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, createSimulationRequestDTO.getSimulationName());
            statement.setString(5, sessionUUID);
            statement.setBoolean(6, true);
            statement.executeUpdate();
        } catch (NoSuchAlgorithmException | SQLException e) {
            throw new RuntimeException(e);
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

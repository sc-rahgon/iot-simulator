package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.neos.simulator.*;
import com.neos.simulator.dto.AttributeRequestDTO;
import com.neos.simulator.dto.CreateSimulationRequestDTO;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.request.EventBuffer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.neos.simulator.constants.SimulatorConstants.parameterConfig;

public class GenerateSampleResponse implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(GenerateSampleResponse.class);

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
            String sampleResponse = generateSampleResponse(simulation);
            if(sampleResponse != null) {
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, sampleResponse.getBytes().length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(sampleResponse.getBytes());
                os.close();
            } else {
                httpExchange.sendResponseHeaders(500, -1);
            }
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }

    private String generateSampleResponse(Simulation simulation) {
        DeviceSetting deviceSetting = simulation.getDevice();
        DeviceSetting gatewaySetting = simulation.getGateway();
        List<Map<String, Object>> arr = new ArrayList<>();
        int paddingLengthDevice = String.valueOf(deviceSetting.getCount()).length();
        int paddingLengthGateway = String.valueOf(gatewaySetting.getCount()).length();
        Random random = new Random();

        for (int i = 1; i <= deviceSetting.getCount(); i++) {
            int devicesPerGateway = (int) Math.ceil((double) deviceSetting.getCount() / gatewaySetting.getCount());
            int r = random.nextInt((int) deviceSetting.getCount()) + 1;
            String device;
            int gatewayIndex;
            String gateway;
            device = deviceSetting.getPrefix() + String.format("%0" + paddingLengthDevice + "d", i);
            gatewayIndex = (int) Math.ceil((double) i / devicesPerGateway);
            gateway = gatewaySetting.getPrefix() + String.format("%0" + paddingLengthGateway + "d", gatewayIndex);
            Map<String, Object> event = null;
            try {
                event = generateEvent(device, gateway, simulation.getAttributes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (event != null) {
                arr.add(event);
            }

            if (i / devicesPerGateway == 0 || i == deviceSetting.getCount()) {
                if (arr.size() > 0) {
                    return new Gson().toJson(arr);
                }
            }
        }
        return null;
    }

    public Map<String, Object> generateEvent(String device, String gateway, Map<String, Object> attributes) throws IOException {
        RandomJsonGenerator generator = new RandomJsonGenerator(attributes);
        Map<String, Object> data = generator.generateData();
        data.put("device", device);
        data.put("gateway", gateway);
        return data;
    }

    private Simulation setSimulationConfig(CreateSimulationRequestDTO createSimulationRequestDTO) {
        Simulation simulation = new Simulation();
        DeviceSetting deviceSetting = new DeviceSetting();
        deviceSetting.setPrefix(createSimulationRequestDTO.getDevice().get("prefix"));
        deviceSetting.setCount(Long.parseLong(createSimulationRequestDTO.getDevice().get("count")));
        simulation.setDevice(deviceSetting);
        Map<String, Object> requestBodyAttribute = createSimulationRequestDTO.getAttributes();
        Map<String, Object> attributes = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Map.Entry<String, Object> entry : requestBodyAttribute.entrySet()) {
            AttributeRequestDTO attributeRequestDTO;
            try {
                attributeRequestDTO = objectMapper.readValue(objectMapper.writeValueAsString(entry.getValue()), AttributeRequestDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(Objects.nonNull(attributeRequestDTO)) {
                attributes.put(entry.getKey(), attributeRequestDTO.getFunction());
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if(!attributes.containsKey("timestamp")) {
            if(createSimulationRequestDTO.getDurationDefine().equals("false")) {
                attributes.put("timestamp", "timestamp()");
            } else {
                attributes.put("timestamp", parameterConfig.replace("{type}", "timestamp")
                        .replace("{lowerLimit}", ZonedDateTime.ofInstant(createSimulationRequestDTO.getDates().get(0).toInstant(), ZoneOffset.UTC).format(formatter))
                        .replace("{upperLimit}", ZonedDateTime.ofInstant(createSimulationRequestDTO.getDates().get(1).toInstant(), ZoneOffset.UTC).format(formatter))
                        .replace("{frequency}", String.valueOf(createSimulationRequestDTO.getSimulationFrequency())));

            }
        }
        simulation.setAttributes(attributes);
        simulation.setFrequency(createSimulationRequestDTO.getSimulationFrequency());
        simulation.setGateway(new DeviceSetting(Long.parseLong(createSimulationRequestDTO.getDevice().get("count")), createSimulationRequestDTO.getDevice().get("prefix")));
        simulation.setType("batch");
        return simulation;
    }
}

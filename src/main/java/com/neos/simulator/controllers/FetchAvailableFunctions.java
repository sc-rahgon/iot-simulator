package com.neos.simulator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.neos.simulator.constants.SimulatorConstants;
import com.neos.simulator.dto.FetchAvailableFunctionResponseDTO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchAvailableFunctions implements HttpHandler {

    private static final Logger LOGGER = LogManager.getLogger(FetchAvailableFunctions.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equalsIgnoreCase("GET")) {
            HashMap<String, String> functions = SimulatorConstants.FUNCTIONS;
            List<FetchAvailableFunctionResponseDTO> availableFunctions = new ArrayList<>();
            for(Map.Entry<String, String> entry : functions.entrySet()) {
                FetchAvailableFunctionResponseDTO fetchAvailableFunctionResponseDTO = new FetchAvailableFunctionResponseDTO();
                fetchAvailableFunctionResponseDTO.setFunction(entry.getKey());
                fetchAvailableFunctionResponseDTO.setDescription(entry.getValue());
                availableFunctions.add(fetchAvailableFunctionResponseDTO);
            }
            String response = new Gson().toJson(availableFunctions);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }
}

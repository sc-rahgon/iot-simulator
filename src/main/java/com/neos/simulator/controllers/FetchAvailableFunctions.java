package com.neos.simulator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.neos.simulator.constants.SimulatorConstants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FetchAvailableFunctions implements HttpHandler {

    private static final Logger LOGGER = LogManager.getLogger(FetchAvailableFunctions.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equalsIgnoreCase("GET")) {
            List<String> functions = SimulatorConstants.FUNCTIONS;
            String response = new Gson().toJson(functions);
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

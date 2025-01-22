package com.neos.simulator.controllers;

import com.neos.simulator.SimulationRunner;
import com.neos.simulator.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class StopSimulationEvent implements HttpHandler {
    private SimulationRunner runner;
    private static final Logger LOGGER = LogManager.getLogger(StopSimulationEvent.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            Map<String,String> query = Utils.parseQuery((httpExchange.getRequestURI().getQuery()));
            runner = new SimulationRunner();
            if(query.containsKey("email")) {
                runner.stopSimulation(query.get("email"));
            } else {
                throw new RuntimeException("Invalid email ID provided");
            }
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }
}

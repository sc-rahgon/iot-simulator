package com.neos.simulator.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ValuesForTime implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HashMap<String, Long> values = new LinkedHashMap<>();
        values.put("1 Minute", 60000L);
        values.put("5 Minutes", 60000L * 5);
        values.put("10 Minutes", 60000L * 10);
        values.put("15 Minutes", 60000L * 15);
        values.put("30 Minutes", 60000L * 30);
        values.put("60 Minutes", 60000L * 60);
        values.put("8 Hours", 60000L * 60 * 8);
        values.put("24 Hours", 60000L * 60 * 24);

        String response = new Gson().toJson(values);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

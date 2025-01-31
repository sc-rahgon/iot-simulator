package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.neos.simulator.Main;
import com.neos.simulator.dto.FetchAllSimulationRequestDTO;
import com.neos.simulator.dto.StopSimulationRequestDTO;
import com.neos.simulator.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.springframework.security.core.parameters.P;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchAllSimulation implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {
            InputStream inputStream = httpExchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            ObjectMapper objectMapper = new ObjectMapper();
            FetchAllSimulationRequestDTO fetchAllSimulationRequestDTO = null;
            try {
                fetchAllSimulationRequestDTO = objectMapper.readValue(requestBody, FetchAllSimulationRequestDTO.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("UNABLE to process exception");
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            if(fetchAllSimulationRequestDTO.getIsActive() !=null && fetchAllSimulationRequestDTO.getEmail() != null) {
                basicDBObject.put("isActive", fetchAllSimulationRequestDTO.getIsActive());
                basicDBObject.put("emailId", fetchAllSimulationRequestDTO.getEmail());
            } else if (fetchAllSimulationRequestDTO.getEmail() != null) {
                basicDBObject.put("emailId", fetchAllSimulationRequestDTO.getEmail());
            } else {
                httpExchange.sendResponseHeaders(500, -1);
            }
            MongoClient mongoClient = Main.RequestProcessorData.mongoClient;
            MongoDatabase db = mongoClient.getDatabase("neom_prod");
            MongoCollection<Document> collection = db.getCollection("iot-simulator");
            List<Document> document = collection.find(basicDBObject).into(new ArrayList<>());
            String response = new Gson().toJson(document);
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

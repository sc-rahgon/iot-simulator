package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.neos.simulator.Main;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.dto.CreateSimulationRequestDTO;
import com.neos.simulator.dto.StopSimulationRequestDTO;
import com.neos.simulator.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StopSimulationEvent implements HttpHandler {
    private SimulationRunner runner;
    private static final Logger LOGGER = LogManager.getLogger(StopSimulationEvent.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {
            InputStream inputStream = httpExchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            ObjectMapper objectMapper = new ObjectMapper();
            StopSimulationRequestDTO stopSimulationRequestDTO = null;
            try {
                stopSimulationRequestDTO = objectMapper.readValue(requestBody, StopSimulationRequestDTO.class);
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getStackTrace());
                throw new RuntimeException("UNABLE to process exception");
            }
            runner = new SimulationRunner();
            List<Thread> threads = fetchThread(stopSimulationRequestDTO.getEmail(), stopSimulationRequestDTO.getSimulationName());
            threads.removeAll(Collections.singleton(null));
            if(stopSimulationRequestDTO.getEmail() != null && stopSimulationRequestDTO.getSimulationName() != null) {
                runner.stopSimulation(threads);
                updateRunningStatus(stopSimulationRequestDTO.getEmail(), stopSimulationRequestDTO.getSimulationName());
            } else {
                throw new RuntimeException("Invalid email ID provided");
            }
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }

    private void updateRunningStatus(String email, String simulationName) {
        String sessionUUID = "";
        try {
            sessionUUID = String.valueOf(Utils.generateUUID(email + simulationName));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = Main.RequestProcessorData.connection.prepareStatement("UPDATE SIMULATION_DETAILS\n" +
                    "SET is_active = false \n" +
                    "where session_uuid = ? and email = ? and is_active = ?");
            preparedStatement.setString(1, sessionUUID);
            preparedStatement.setString(2, email);
            preparedStatement.setBoolean(3, true);
            preparedStatement.executeUpdate();

            MongoClient mongoClient = Main.RequestProcessorData.mongoClient;
            MongoDatabase db = mongoClient.getDatabase("neom_prod");
            MongoCollection<Document> collection = db.getCollection("iot-simulator");
            Document document = collection.find(new BasicDBObject("sessionUUID", sessionUUID)).first();
            if(document != null) {
                if(document.containsKey("isActive")) {
                    document.replace("isActive", "false");
                }
                if(document.containsKey("updatedBy")) {
                    document.replace("updatedBy", email);
                }
                if(document.containsKey("updatedAt")) {
                    document.replace("updatedAt", LocalDateTime.now());
                }
            }

            collection.updateOne(Filters.eq("sessionUUID", sessionUUID), // Filter to find the document
                    new Document("$set", document));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Thread> fetchThread(String email, String simulatioName) {
        List<Thread> threads = new ArrayList<>();
        String sessionUUID = "";
        try {
            sessionUUID = String.valueOf(Utils.generateUUID(email + simulatioName));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = Main.RequestProcessorData.connection.prepareStatement("SELECT * FROM SIMULATION_DETAILS where session_uuid = ? and email = ? and is_active = ?");
            preparedStatement.setString(1, sessionUUID);
            preparedStatement.setString(2, email);
            preparedStatement.setBoolean(3, true);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (resultSet.next()) {
                String[] threadIDs = resultSet.getString("thread_details").split(";");
                for(String threadID : threadIDs) {
                    ids.add(Integer.parseInt(threadID));
                }
            }
            ids.forEach(x -> {
                Thread t = Utils.findThreadById(x);
                threads.add(t);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return threads;
    }
}

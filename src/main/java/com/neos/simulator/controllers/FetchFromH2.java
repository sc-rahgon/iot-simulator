package com.neos.simulator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neos.simulator.Main;
import com.neos.simulator.Simulation;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.dto.CreateSimulationRequestDTO;
import com.neos.simulator.request.EventBuffer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FetchFromH2 implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(FetchFromH2.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            try {
                PreparedStatement preparedStatement = Main.RequestProcessorData.connection.prepareStatement("SELECT * FROM SIMULATION_DETAILS");
                ResultSet resultSet = preparedStatement.executeQuery();
                List<HashMap<String, Object>> responseSet = fetchResultSet(resultSet);
                ObjectMapper objectMapper = new ObjectMapper();
                OutputStream os = httpExchange.getResponseBody();
                String response = "";
                if(!responseSet.isEmpty()) {
                    response = objectMapper.writeValueAsString(responseSet);
                }
                os.write(response.getBytes());
                os.close();
            } catch (RuntimeException | SQLException e) {
                throw new RuntimeException(e);
            }

            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.sendResponseHeaders(405, 0);
        }
}

    private List<HashMap<String, Object>> fetchResultSet(ResultSet resultSet) throws SQLException {
        List<HashMap<String, Object>> finalResponse = new ArrayList<>();
        while(resultSet.next()) {
            HashMap<String, Object> response = new HashMap<>();
            try {
                if (resultSet.next()) {
                    response.put("id", resultSet.getInt("id"));
                    response.put("email", resultSet.getString("email"));
                    response.put("thread_details", resultSet.getString("thread_details"));
                    response.put("timestamp", resultSet.getTimestamp("timestamp"));
                    response.put("simulation_name", resultSet.getString("simulation_name"));
                    response.put("session_uuid", resultSet.getString("session_uuid"));
                    response.put("is_active", resultSet.getBoolean("is_active"));
                    finalResponse.add(response);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return finalResponse;
    }
}

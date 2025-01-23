package com.neos.simulator.controllers;

import com.neos.simulator.Main;
import com.neos.simulator.SimulationRunner;
import com.neos.simulator.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StopSimulationEvent implements HttpHandler {
    private SimulationRunner runner;
    private static final Logger LOGGER = LogManager.getLogger(StopSimulationEvent.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            Map<String,String> query = Utils.parseQuery((httpExchange.getRequestURI().getQuery()));
            runner = new SimulationRunner();
            List<Thread> threads = fetchThread(query.get("email"), query.get("simulationName"));
            if(query.containsKey("email") && query.containsKey("simulationName")) {
                runner.stopSimulation(threads);
                updateRunningStatus(query.get("email"), query.get("simulationName"));
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

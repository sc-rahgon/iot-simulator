package com.neos.simulator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.neos.simulator.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neos.simulator.config.Config;
import com.neos.simulator.config.SimulationConfig;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.request.EventBuffer;
import com.neos.simulator.request.RequestProcessor;
import com.neos.simulator.util.JSONConfigReader;

import static com.neos.simulator.constants.SimulatorConstants.INSERT_INTO_SIMULATION_DETAILS;

public class SimulationRunner {
    private static final Logger log = LogManager.getLogger(SimulationRunner.class);

    private Config config;
    private List<EventGenerator> eventGenerators;
    private List<Thread> eventGenThreads;
    private boolean running;
    private List<EventProducer> producers;
    private List<RequestProcessor> requestProcessors;
    private String basePath;
    private Simulation simulation;

    public SimulationRunner(Config config, List<EventProducer> producers, List<RequestProcessor> requestProcessors, String basePath, EventBuffer buffer) {
        this.config = config;
        this.producers = producers;
        this.requestProcessors = requestProcessors;
        this.basePath = basePath;

        eventGenerators = new ArrayList<EventGenerator>();
        eventGenThreads = new ArrayList<Thread>();

        setupSimulation(buffer);
    }

    public SimulationRunner(Config config, List<EventProducer> producers, List<RequestProcessor> requestProcessors, String basePath, EventBuffer buffer, Simulation simulation) {
        this.config = config;
        this.producers = producers;
        this.requestProcessors = requestProcessors;
        this.basePath = basePath;

        eventGenerators = new ArrayList<EventGenerator>();
        eventGenThreads = new ArrayList<Thread>();
        this.simulation = simulation;
        setupSimulationDirect(buffer);
    }

    public SimulationRunner() {
    }

    private void setupSimulation(EventBuffer buffer) {
        running = false;
        for (SimulationConfig simulationConfig : config.getSimulations()) {
            try {
                String simulationConfigPath = this.basePath + "/" + simulationConfig.getSimulationConfig();
                Simulation simulation = JSONConfigReader.readConfig(simulationConfigPath, Simulation.class);

                for (RequestProcessor requestProcessor : requestProcessors) {
                    requestProcessor.setDeviceSetting(simulation.getDevice());
                    requestProcessor.setGatewaySetting(simulation.getGateway());
                }

                final EventGenerator gen = new EventGenerator(simulation, producers, buffer);
                eventGenerators.add(gen);
                eventGenThreads.add(new Thread(gen));
            } catch (IOException ex) {
                log.error("Error reading config: " + simulationConfig.getSimulationName(), ex);
            }
        }
    }

    private void setupSimulationDirect(EventBuffer buffer) {
        running = false;
        try {
            for (RequestProcessor requestProcessor : requestProcessors) {
                requestProcessor.setDeviceSetting(simulation.getDevice());
                requestProcessor.setGatewaySetting(simulation.getGateway());
            }

            final EventGenerator gen = new EventGenerator(simulation, producers, buffer);
            eventGenerators.add(gen);
            eventGenThreads.add(new Thread(gen));
        } catch (RuntimeException ex) {
            log.error("Error reading config: " + ex);
        }

    }

    public void startSimulation(String emailID, String simulationName) {
        log.info("Starting Simulation");
        if (!eventGenThreads.isEmpty()) {
            for (Thread t : eventGenThreads) {
                System.err.println("Thread details:" + t.getId());
                t.start();
            }
            running = true;
            try {
                String sessionUUID = String.valueOf(Utils.generateUUID(emailID + simulationName));
                String threadIDs = eventGenThreads.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining(";"));
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
                statement.setString(1, emailID);
                statement.setString(2, threadIDs);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(4, simulationName);
                statement.setString(5, sessionUUID);
                statement.setBoolean(6, running);
                statement.executeUpdate();
            } catch (NoSuchAlgorithmException | SQLException e) {
                throw new RuntimeException(e);
            }
            Main.RequestProcessorData.cache.put(emailID, eventGenThreads, 200000000);
        }
    }

    public void stopSimulation() {
        log.error("Stopping Simulation");
        for (Thread t : eventGenThreads) {
            t.interrupt();
        }
        for (EventProducer p : producers) {
            p.stop();
        }
        running = false;
    }

    public void stopSimulation(String email, String simulationName) {
        log.error("Stopping Simulation");
        String sessionUUID = "";
        try {
            sessionUUID = String.valueOf(Utils.generateUUID(email + simulationName));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        List<Thread> threads = new ArrayList<>();
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
        threads.forEach(Thread::interrupt);
        running = false;

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

    public boolean isRunning() {
        return running;
    }
}

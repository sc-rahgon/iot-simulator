package com.neos.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neos.simulator.config.Config;
import com.neos.simulator.config.SimulationConfig;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.request.EventBuffer;
import com.neos.simulator.request.RequestProcessor;
import com.neos.simulator.util.JSONConfigReader;


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

    public void startSimulation(String emailID) {
        log.info("Starting Simulation");
        if (!eventGenThreads.isEmpty()) {
            Main.RequestProcessorData.cache.put(emailID, eventGenThreads, 200000000);
            for (Thread t : eventGenThreads) {
                System.err.println("Thread details:" + t.getId());
                t.start();
            }
            running = true;
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

    public void stopSimulation(String email) {
        log.error("Stopping Simulation");
        if (Main.RequestProcessorData.cache.containsKey(email)) {
            List<Thread> t = (List<Thread>) Main.RequestProcessorData.cache.get(email);
            t.forEach(Thread::interrupt);
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}

package com.neos.simulator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for configuring the simulation to be executed.
 * 
 * <p>
 * An example of the simulation configuration JSON file can be:
 * </p>
 * 
 * <pre>
 * "simulations": [
 *     {
 *         "simulationName": "test",
 *         "simulationConfig": "simulation.json",
 *         "customTypeHandlers": [
 *             "com.neos.custom.types",
 *             "org.neos.custom.example.types"
 *         ]
 *     }
 * ]
 * </pre>
 */
public class SimulationConfig {
    private String simulationName;
    private String simulationConfig;
    private List<String> customTypeHandlers;

    public SimulationConfig(String simulationName, String simulationConfig, List<String> customTypeHandlers) {
        this.simulationName = simulationName;
        this.simulationConfig = simulationConfig;
        this.customTypeHandlers = customTypeHandlers != null ? customTypeHandlers : new ArrayList<>();
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getSimulationConfig() {
        return simulationConfig;
    }

    public void setSimulationConfig(String simulationConfig) {
        this.simulationConfig = simulationConfig;
    }

    public List<String> getCustomTypeHandlers() {
        return customTypeHandlers;
    }

    public void setCustomTypeHandlers(List<String> customTypeHandlers) {
        this.customTypeHandlers = customTypeHandlers != null ? customTypeHandlers : new ArrayList<>();
    }
}

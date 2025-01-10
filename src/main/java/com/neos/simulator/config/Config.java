package com.neos.simulator.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents the configuration for simulations and producers.
 * An example of a complete Simulation Configuration file is shown below:
 *
 * exampleSimConfig.json:
 * {
 *     "workflows": [{
 *             "workflowName": "test",
 *             "workflowFilename": "exampleWorkflow.json"
 *         }],
 *     "producers": [{
 *             "type": "kafka",
 *             "broker.server": "192.168.59.103",
 *             "broker.port": 9092,
 *             "topic": "logevent",
 *             "sync": false
 *     },{
 *         "type":"logger"
 *     }]
 * }
 */
public class Config {
    private List<SimulationConfig> simulations;
    private List<Map<String, Object>> producers;
    private List<Map<String, Object>> requestProcessors;

    public Config() {
        this.simulations = new ArrayList<>();
        this.producers = new ArrayList<>();
    }

    public Config(List<SimulationConfig> simulations, List<Map<String, Object>> producers, List<Map<String, Object>> requestProcessors) {
        this.simulations = simulations;
        this.producers = producers;
        this.requestProcessors = requestProcessors;
    }

    public List<SimulationConfig> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<SimulationConfig> simulations) {
        this.simulations = simulations;
    }

    public List<Map<String, Object>> getProducers() {
        return producers;
    }

    public void setProducers(List<Map<String, Object>> producers) {
        this.producers = producers;
    }

	public List<Map<String, Object>> getRequestProcessors() {
		return requestProcessors;
	}

	public void setRequestProcessors(List<Map<String, Object>> requestProcessors) {
		this.requestProcessors = requestProcessors;
	}
}


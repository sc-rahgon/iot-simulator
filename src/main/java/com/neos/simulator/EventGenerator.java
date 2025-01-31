
package com.neos.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.neos.simulator.config.SimulationConfig;
import com.neos.simulator.producer.EventProducer;
import com.neos.simulator.request.EventBuffer;

public class EventGenerator implements Runnable {
    private static final Logger log = LogManager.getLogger(EventGenerator.class);

    private Simulation simulation;
    private boolean running;
    private List<EventProducer> producers;
    private EventBuffer buffer;
    private String topic;

    public EventGenerator(Simulation simulation, List<EventProducer> producers, EventBuffer buffer, String topic) {
        this.simulation = simulation;
        this.producers = producers;
        this.buffer = buffer;
        this.topic = topic;

    }

    private void runSimulation() {
        while (running) {
            log.info("Generate event");
            generateEventLoop();
            try {
                Thread.sleep(simulation.getFrequency());
            } catch (InterruptedException ie) {
                setRunning(false);
                log.error("Error in sleep", ie);
            }
        }

    }

    public Map<String, Object> generateEvent(String device, String gateway, Map<String, Object> attributes) throws IOException {
        RandomJsonGenerator generator = new RandomJsonGenerator(attributes);
        Map<String, Object> data = generator.generateData();
        data.put("device", device);
        data.put("gateway", gateway);
        return data;
    }

    private String createJsonString(Object data) {
        return new Gson().toJson(data).toString();
    }

    private void generateEventLoop() {
        Random random = new Random();

        DeviceSetting deviceSetting = simulation.getDevice();
        DeviceSetting gatewaySetting = simulation.getGateway();

        int devicesPerGateway = (int) Math.ceil((double) deviceSetting.getCount() / gatewaySetting.getCount());


        int paddingLengthDevice = String.valueOf(deviceSetting.getCount()).length();
        int paddingLengthGateway = String.valueOf(gatewaySetting.getCount()).length();

        switch (simulation.getType()) {
            case "sequential":
                for (int i = 1; i <= deviceSetting.getCount(); i++) {
                    String device = deviceSetting.getPrefix() + String.format("%0" + paddingLengthDevice + "d", i);
                    int gatewayIndex = (int) Math.ceil((double) i / devicesPerGateway);
                    String gateway = gatewaySetting.getPrefix() + String.format("%0" + paddingLengthGateway + "d", gatewayIndex);
                    Map<String, Object> event = null;
                    try {
                        event = generateEvent(device, gateway, simulation.getAttributes());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (event != null) {
                        String e = createJsonString(event);
                        for (EventProducer p : producers) {
                            if (topic != null) {
                                p.publish(e, topic);
                            } else {
                                p.publish(e);
                            }
                        }
                        buffer.addEvent(device, e);
                        buffer.addEvent(gateway, e);
                    }
                }
                break;
            case "random":
                int r = random.nextInt((int) deviceSetting.getCount()) + 1;
                String device = deviceSetting.getPrefix() + String.format("%0" + paddingLengthDevice + "d", r);
                int gatewayIndex = (int) Math.ceil((double) r / devicesPerGateway);
                String gateway = gatewaySetting.getPrefix() + String.format("%0" + paddingLengthGateway + "d", gatewayIndex);
                Map<String, Object> event = null;
                try {
                    event = generateEvent(device, gateway, simulation.getAttributes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (event != null) {
                    String e = createJsonString(event);
                    for (EventProducer p : producers) {
                        if (topic != null) {
                            p.publish(e, topic);
                        } else {
                            p.publish(e);
                        }
                    }

                    buffer.addEvent(device, e);
                    buffer.addEvent(gateway, e);
                }

                break;
            case "batch":
                List<Map<String, Object>> arr = new ArrayList<>();
                for (int i = 1; i <= deviceSetting.getCount(); i++) {
                    device = deviceSetting.getPrefix() + String.format("%0" + paddingLengthDevice + "d", i);
                    gatewayIndex = (int) Math.ceil((double) i / devicesPerGateway);
                    gateway = gatewaySetting.getPrefix() + String.format("%0" + paddingLengthGateway + "d", gatewayIndex);
                    event = null;
                    try {
                        event = generateEvent(device, gateway, simulation.getAttributes());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (event != null) {
                        arr.add(event);
                    }

                    if (i / devicesPerGateway == 0 || i == deviceSetting.getCount()) {
                        if (arr.size() > 0) {
                            String e = createJsonString(arr);
                            for (EventProducer p : producers) {
                                if (topic != null) {
                                    p.publish(e, topic);
                                } else {
                                    p.publish(e);
                                }
                            }

                            buffer.addEvent(gateway, e);

                        }

                    }

                }
                break;
            default:
                System.out.println("Unknown event type: " + simulation.getType());
        }

    }


    public void run() {
        try {
            setRunning(true);
            runSimulation();
            setRunning(false);
        } catch (Throwable ie) {
            log.fatal("Exception occured causing the Generator to shutdown", ie);
            setRunning(false);
        }
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

}

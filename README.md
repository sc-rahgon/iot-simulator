# IoT Device Simulator Documentation

## Overview

The IoT Device Simulator is a tool designed to generate configurable streams of JSON data to emulate IoT devices or other data sources.

### Key Features

- **JSON Data Generation:** Generates JSON documents using predefined JSON schemas.
- **Randomized Data:** Supports random data generation for dynamic simulation.
- **Streaming Support:** Streams JSON data to various endpoints such as log files, Kafka, HTTP, and MQTT.
- **Configurable Timing:** Allows customization of event generation timing and order.

------

## Configuration

### Simulation Configuration

```json
{
  "simulations": [
    {
      "simulationName": "test",
      "simulationConfig": "simulation.json",
      "customTypeHandlers": []
    }
  ],
  "producers": [
    {
      "type": "logger"
    }
  ],
  "requestProcessors": [
    {
      "type": "httpRequest"
    }
  ]
}
```

### Simulation Definition

```json
{
  "device": {
    "count": 10,
    "prefix": "EM"
  },
  "gateway": {
    "count": 2,
    "prefix": "G"
  },
  "frequency": 5000,
  "type": "batch",
  "attributes": {
    "timestamp": "timestamp()",
    "voltage": "integer(100, 240)", 
    "current": "double(0.0, 100.0)",
    "power": "double(0.0, 5000.0)",
    "energy": "double(0.0, 10000.0)",
    "temperature": "double(20.0, 40.0)"
  }
}
```

------

## Building the Project

1. Clean and Install:

   Run the following command to clean and build the project:

   ```bash
   mvn clean install
   ```

------

## Running the Simulator

1. Start the Simulator:

   Use the following command to run the simulator:

   ```bash
   java -jar target/iot-simulator.jar
   ```
package com.neos.simulator.request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.neos.simulator.controllers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neos.simulator.DeviceSetting;
import com.neos.simulator.Simulation;
import com.sun.net.httpserver.HttpExchange;

public class HttpRequestProcessor extends RequestProcessor {

    private static final Logger log = LogManager.getLogger(HttpRequestProcessor.class);

    public HttpRequestProcessor(EventBuffer buffer) throws IOException {
    	this.setBuffer(buffer);
    	
     // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(13150), 0);

        // Create and bind handlers for the routes
        server.createContext("/getDevices", new GetDevicesHandler());
        server.createContext("/getGateways", new GetDevicesHandler());
        server.createContext("/getData", new GetDataHandler());
        server.createContext("/startSimulation", new CreateDeviceSimulationEvent());
        server.createContext("/stopSimulation", new StopSimulationEvent());
        server.createContext("/fetchAllData", new FetchFromH2());
        server.createContext("/list-all-functions", new FetchAvailableFunctions());
        server.createContext("/time-values-in-millis", new ValuesForTime());
        server.createContext("/generate-sample-response", new GenerateSampleResponse());
        server.createContext("/fetch-all-simulator-for-user", new FetchAllSimulation());

        // Start the server
        server.start();
        System.out.println("Server started at http://localhost:13150");
    	
    }
    

    // Handler for /getDevices endpoint
    private class GetDevicesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Create a list of devices
            List<String> devices = new ArrayList<>();

            // Convert the list to a JSON-like string
            JsonArray arr = new JsonArray();
            long deviceCount = getDeviceSetting().getCount();
            String devicePrefix = getDeviceSetting().getPrefix();
        	int paddingLengthDevice= String.valueOf(deviceCount).length();
        	
        	long gatewayCount = 0;
            String gatewayPrefix = "";
        	int paddingLengthGateway= 0;
        	int devicesPerGateway = (int) deviceCount;
        	if (getGatewaySetting() != null) {
        		gatewayCount = getGatewaySetting().getCount();
        		gatewayPrefix = getGatewaySetting().getPrefix();
        		paddingLengthGateway= String.valueOf(gatewayCount).length();
        		
                devicesPerGateway = (int) Math.ceil((double) deviceCount / gatewayCount);
        	}
        	
            for (int i=1; i<= deviceCount ; i++) {
            	JsonObject o = new JsonObject();
            	o.addProperty("device", devicePrefix + String.format("%0" + paddingLengthDevice + "d", i));
            	            	
            	 int gatewayIndex = (int) Math.ceil((double) i / devicesPerGateway);
              
             	o.addProperty("gateway", gatewayPrefix + String.format("%0" + paddingLengthGateway + "d", gatewayIndex));

            	arr.add(o);
            }

            String response = arr.toString();

            // Set the response headers and send the response
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    private class GetGatewaysHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Create a list of devices
            List<String> devices = new ArrayList<>();

            // Convert the list to a JSON-like string
            JsonArray arr = new JsonArray();
            long count = getGatewaySetting().getCount();
            String prefix = getGatewaySetting().getPrefix();
        	int paddingLength = String.valueOf(count).length();
            for (int i=1; i<= count ; i++) {
            	JsonObject o = new JsonObject();
            	o.addProperty("gateway", prefix + String.format("%0" + paddingLength + "d", i));
            	arr.add(o);
            }

            String response = arr.toString();
            
            // Set the response headers and send the response
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Handler for /getData endpoint
    private class GetDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Extract device from the query parameter "device"
            String query = exchange.getRequestURI().getQuery();
            String device = null;

            if (query != null && query.startsWith("device=")) {
                device = query.split("=")[1];
            }

            // If the device is null, send an error response
            if (device == null) {
                String errorResponse = "{\"error\": \"Device parameter is required\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, errorResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
                return;
            }

            // Simulate retrieving data for the given device
            String[] events = getBuffer().getLastEvents(device);
            String response = "";
            if (events.length > 0) {
            	response = events[events.length-1];
            }
            // Send the response
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}

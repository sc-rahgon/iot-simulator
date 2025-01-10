package com.neos.simulator.request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EventBuffer {
    private Map<String, LinkedList<String>> events;
    private int maxSize;

    public EventBuffer() {
        this.maxSize = 5;
        this.events = new HashMap<>();
    }
    
    public EventBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.events = new HashMap<>();
    }

    public void addEvent(String identifier, String event) {
    	if (events.containsKey(identifier)) {
    		LinkedList<String> ll = events.get(identifier);
    		if (ll.size() == maxSize) {
                ll.removeFirst();
            }
    		ll.addLast(event);
    	}else {
    		LinkedList<String> ll = new LinkedList<>();
    		ll.addLast(event);
    		events.put(identifier, ll);
    	}
    }

    public String[] getLastEvents(String identifier) {
    	if (events.containsKey(identifier)) {
    		return events.get(identifier).toArray(new String[0]);
    	}else {
    		return new String[] {};
    	}
    }
  
}

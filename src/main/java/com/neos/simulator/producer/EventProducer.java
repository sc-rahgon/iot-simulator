package com.neos.simulator.producer;

public abstract class EventProducer {		
	public abstract void publish(String event);
	public abstract void publish(String event, String topic);
	public abstract void stop();
}

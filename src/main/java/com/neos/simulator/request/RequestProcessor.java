package com.neos.simulator.request;

import com.neos.simulator.DeviceSetting;

public abstract class RequestProcessor {	
	private EventBuffer buffer;
    private DeviceSetting deviceSetting;
    private DeviceSetting gatewaySetting;
	public EventBuffer getBuffer() {
		return buffer;
	}
	public void setBuffer(EventBuffer buffer) {
		this.buffer = buffer;
	}
	public DeviceSetting getDeviceSetting() {
		return deviceSetting;
	}
	public void setDeviceSetting(DeviceSetting deviceSetting) {
		this.deviceSetting = deviceSetting;
	}
	public DeviceSetting getGatewaySetting() {
		return gatewaySetting;
	}
	public void setGatewaySetting(DeviceSetting gatewaySetting) {
		this.gatewaySetting = gatewaySetting;
	}
    
}

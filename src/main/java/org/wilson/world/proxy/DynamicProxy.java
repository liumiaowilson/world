package org.wilson.world.proxy;

import org.wilson.world.util.TimeUtils;

/**
 * Only HTTP proxy
 * 
 * @author mialiu
 *
 */
public class DynamicProxy {
	
	public String host;
	
	public int port;
	
	//negative value means unknown
	public long echoTime = -1;
	
	public String externalIP;
	
	public String getEchoTimeDisplay() {
		if(echoTime < 0) {
			return "Unknown";
		}
		else {
			return TimeUtils.getTimeReadableString(echoTime);
		}
	}
	
	public String getExternalIPDisplay() {
		if(externalIP == null) {
			return "Unknown";
		}
		else {
			return externalIP;
		}
	}
}

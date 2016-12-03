package org.wilson.world.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.WebManager;

public class IPJob extends SystemWebJob {
	public static final String EXTERNAL_IP = "system.external.ip";
	public static final String CHECK_IP_SITE = "http://checkip.amazonaws.com";
	
	@Override
	public void run() throws Exception {
		URL url = new URL(CHECK_IP_SITE);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String ip = in.readLine();
		
		if(StringUtils.isNotBlank(ip)) {
			WebManager.getInstance().put(EXTERNAL_IP, ip);
		}
	}

}

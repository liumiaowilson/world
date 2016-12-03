package org.wilson.world.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.IPJob;
import org.wilson.world.web.NullWebJobMonitor;
import org.wilson.world.web.SystemWebJob;
import org.wilson.world.web.WebJobMonitor;

import net.sf.json.JSONObject;

public class DynaProxyJob extends SystemWebJob {
	private static final Logger logger = Logger.getLogger(DynaProxyJob.class);

	private boolean needCheckEchoTime() {
		return ConfigManager.getInstance().getConfigAsBoolean("dynamic_proxy.echo_time.check.default", true);
	}
	
	@Override
	public void run() throws Exception {
		WebJobMonitor monitor = this.getMonitor();
		if(monitor == null) {
			monitor = new NullWebJobMonitor();
		}
		
		Document doc = WebManager.getInstance().parse("http://www.gatherproxy.com/", "Mozilla");
        String content = doc.toString();
        String start = "gp.insertPrx(";
        String end = ");";
        int pos = 0;
        int pos2 = 0;
        List<DynamicProxy> proxies = new ArrayList<DynamicProxy>();
        while(true) {
        	try {
        		pos = content.indexOf(start, pos2);
            	if(pos < 0) {
            		break;
            	}
            	pos2 = content.indexOf(end, pos);
            	if(pos2 < 0) {
            		break;
            	}
            	String piece = content.substring(pos + start.length(), pos2);
            	JSONObject obj = JSONObject.fromObject(piece);
            	String ip = obj.getString("PROXY_IP");
            	String portStr = obj.getString("PROXY_PORT");
            	int port = Integer.parseInt(portStr, 16);
            	DynamicProxy proxy = new DynamicProxy();
            	proxy.host = ip;
            	proxy.port = port;
            	proxies.add(proxy);
        	}
        	catch(Exception e) {
        		logger.error(e);
        	}
        }
		
        DefaultDynamicProxyProvider provider = DefaultDynamicProxyProvider.getInstance();
    	provider.clear();
        if(this.needCheckEchoTime()) {
        	monitor.adjust(proxies.size());
        	
        	for(DynamicProxy proxy : proxies) {
        		try {
        			long startTime = System.currentTimeMillis();
            		String ip = WebManager.getInstance().getContentWithProxy(IPJob.CHECK_IP_SITE, proxy.host, proxy.port);
            		long endTime = System.currentTimeMillis();
            		if(StringUtils.isNotBlank(ip)) {
            			proxy.externalIP = ip;
            			proxy.echoTime = endTime - startTime;
                		provider.addProxy(proxy);
            		}
            		
            		if(monitor.isStopRequired()) {
            			monitor.stop();
            			return;
            		}
            		monitor.progress(1);
        		}
        		catch(Exception e) {
        			logger.error(e);
        		}
        	}
        }
        else {
        	for(DynamicProxy proxy : proxies) {
        		provider.addProxy(proxy);
        	}
        }
	}

}

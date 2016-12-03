package org.wilson.world.proxy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.IPJob;
import org.wilson.world.web.NullWebJobMonitor;
import org.wilson.world.web.SystemWebJob;
import org.wilson.world.web.WebJobMonitor;

public class DynaProxyJob extends SystemWebJob {
	private static final Logger logger = Logger.getLogger(DynaProxyJob.class);

	private boolean needCheckEchoTime() {
		return false;
	}
	
	@Override
	public void run() throws Exception {
		WebJobMonitor monitor = this.getMonitor();
		if(monitor == null) {
			monitor = new NullWebJobMonitor();
		}
		
		Document doc = WebManager.getInstance().parse("http://www.us-proxy.org/", "Mozilla");
        Elements elements = doc.select("table#proxylisttable tbody tr");
        if(!elements.isEmpty()) {
        	DefaultDynamicProxyProvider provider = DefaultDynamicProxyProvider.getInstance();
        	provider.clear();
        	
        	monitor.adjust(elements.size());
            for(int i = 0; i < elements.size(); i++) {
            	Element element = elements.get(i);
            	Elements tds = element.select("td");
            	if(tds.size() >= 2) {
            		try {
            			String host = tds.get(0).text().trim();
                		String port = tds.get(1).text().trim();
                		DynamicProxy proxy = new DynamicProxy();
                		proxy.host = host;
                		proxy.port = Integer.parseInt(port);
                		
                		if(this.needCheckEchoTime()) {
                			long start = System.currentTimeMillis();
                    		String ip = WebManager.getInstance().getContentWithProxy(IPJob.CHECK_IP_SITE, proxy.host, proxy.port);
                    		long end = System.currentTimeMillis();
                    		if(StringUtils.isNotBlank(ip)) {
                    			proxy.externalIP = ip;
                    			proxy.echoTime = end - start;
                        		provider.addProxy(proxy);
                    		}
                		}
                		else {
                			provider.addProxy(proxy);
                		}
            		}
            		catch(Exception e) {
            			logger.error(e);
            		}
            	}
            }
        }
	}

}

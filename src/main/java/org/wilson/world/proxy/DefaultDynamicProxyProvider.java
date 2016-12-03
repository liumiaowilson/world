package org.wilson.world.proxy;

import java.util.ArrayList;
import java.util.List;

public class DefaultDynamicProxyProvider extends AbstractDynamicProxyProvider {
	private static DefaultDynamicProxyProvider instance;
	private List<DynamicProxy> proxies = new ArrayList<DynamicProxy>();
	
	private DefaultDynamicProxyProvider() {
		this.setName("Default Dynamic Proxy Provider");
	}
	
	public static DefaultDynamicProxyProvider getInstance() {
		if(instance == null) {
			instance = new DefaultDynamicProxyProvider();
		}
		
		return instance;
	}
	
	@Override
	public List<DynamicProxy> getProxies() {
		return this.proxies;
	}

	public void clear() {
		this.proxies.clear();
	}
	
	public void addProxy(DynamicProxy proxy) {
		if(proxy != null) {
			this.proxies.add(proxy);
		}
	}
	
	public void removeProxy(DynamicProxy proxy) {
		if(proxy != null) {
			this.proxies.remove(proxy);
		}
	}
}

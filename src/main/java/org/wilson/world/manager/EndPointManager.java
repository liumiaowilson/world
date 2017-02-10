package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.endpoint.DemoEndPoint;
import org.wilson.world.endpoint.EndPoint;
import org.wilson.world.endpoint.EndPointInfo;
import org.wilson.world.endpoint.EndPointMethod;
import org.wilson.world.endpoint.EndPointMethodScope;
import org.wilson.world.java.JavaExtensionListener;

public class EndPointManager implements JavaExtensionListener<EndPoint> {
	private static EndPointManager instance;
	
	private Map<String, EndPoint> eps = new HashMap<String, EndPoint>();
	
	private EndPointManager() {
		this.loadSystemEndPoints();
		
		ExtManager.getInstance().addJavaExtensionListener(this);
	}
	
	public static EndPointManager getInstance() {
		if(instance == null) {
			instance = new EndPointManager();
		}
		
		return instance;
	}
	
	private void loadSystemEndPoints() {
		this.addEndPoint(new DemoEndPoint());
	}
	
	public void addEndPoint(EndPoint ep) {
		if(ep != null && ep.getName() != null) {
			this.eps.put(ep.getName(), ep);
		}
	}
	
	public void removeEndPoint(EndPoint ep) {
		if(ep != null && ep.getName() != null) {
			this.eps.remove(ep);
		}
	}
	
	public List<EndPoint> getEndPoints() {
		return new ArrayList<EndPoint>(this.eps.values());
	}
	
	public EndPoint getEndPoint(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.eps.get(name);
	}
	
	@Override
	public Class<EndPoint> getExtensionClass() {
		return EndPoint.class;
	}

	@Override
	public void created(EndPoint t) {
		this.addEndPoint(t);
	}

	@Override
	public void removed(EndPoint t) {
		this.removeEndPoint(t);
	}
	
	public List<EndPointInfo> getEndPointInfos() {
		List<EndPointInfo> ret = new ArrayList<EndPointInfo>();
		for(EndPoint ep : this.getEndPoints()) {
			for(EndPointMethod method : ep.getMethods()) {
				EndPointInfo info = new EndPointInfo();
				info.name = ep.getName() + "/" + method.getName();
				info.httpMethod = method.getType().name();
				info.uri = "/api/endpoint/";
				if(EndPointMethodScope.Public == method.getScope()) {
					info.uri += "public/";
				}
				info.uri += info.name;
				ret.add(info);
			}
		}
		
		return ret;
	}
}

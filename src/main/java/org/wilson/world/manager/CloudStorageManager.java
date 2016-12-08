package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cloud.CloudStorageService;
import org.wilson.world.cloud.DefaultCloudStorageService;
import org.wilson.world.java.JavaExtensionListener;

public class CloudStorageManager implements JavaExtensionListener<CloudStorageService> {
	private static CloudStorageManager instance;
	
	private Map<String, CloudStorageService> services = new HashMap<String, CloudStorageService>();
	
	private CloudStorageManager() {
		this.loadSystemCloudStorageServices();
		
		ExtManager.getInstance().addJavaExtensionListener(this);
	}
	
	public static CloudStorageManager getInstance() {
		if(instance == null) {
			instance = new CloudStorageManager();
		}
		
		return instance;
	}
	
	private void loadSystemCloudStorageServices() {
		this.addCloudStorageService(new DefaultCloudStorageService());
	}
	
	public void addCloudStorageService(CloudStorageService service) {
		if(service != null) {
			this.services.put(service.getName(), service);
		}
	}
	
	public void removeCloudStorageService(CloudStorageService service) {
		if(service != null) {
			this.services.remove(service.getName());
		}
	}

	@Override
	public Class<CloudStorageService> getExtensionClass() {
		return CloudStorageService.class;
	}

	@Override
	public void created(CloudStorageService t) {
		this.addCloudStorageService(t);
	}

	@Override
	public void removed(CloudStorageService t) {
		this.removeCloudStorageService(t);
	}
	
	public List<CloudStorageService> getCloudStorageServices() {
		return new ArrayList<CloudStorageService>(this.services.values());
	}
	
	public CloudStorageService getCloudStorageService(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.services.get(name);
	}
}

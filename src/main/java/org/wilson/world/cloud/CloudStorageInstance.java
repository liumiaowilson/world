package org.wilson.world.cloud;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.exception.DataException;
import org.wilson.world.manager.CloudStorageManager;
import org.wilson.world.model.CloudStorageData;

import net.sf.json.JSONObject;

public class CloudStorageInstance {
	private int id;
	private String name;
	private CloudStorageService service;
	
	public CloudStorageInstance(CloudStorageData data) {
		if(data == null) {
			throw new DataException("CloudStorageData should be provided.");
		}
		
		this.init(data);
	}
	
	private void init(CloudStorageData data) {
		this.id = data.id;
		this.name = data.name;
		
		service = CloudStorageManager.getInstance().getCloudStorageService(data.service);
		if(service == null) {
			throw new DataException("CloudStorageService does not exist for [" + data.service + "].");
		}
		
		JSONObject config = JSONObject.fromObject(data.content);
		Map<String, String> vars = new HashMap<String, String>();
		for(Object keyObj : config.keySet()) {
			String key = (String) keyObj;
			String value = config.getString(key);
			vars.put(key, value);
		}
		service.init(vars);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public CloudStorageService getService() {
		return this.service;
	}
}

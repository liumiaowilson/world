package org.wilson.world.cloud;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CloudStorageAsset {
	public String id;
	
	public String name;
	
	public Map<String, String> metadata = new HashMap<String, String>();
	
	public String getString(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.metadata.get(name);
	}
	
	public int getInt(String name) {
		if(StringUtils.isBlank(name)) {
			return 0;
		}
		
		try {
			return Integer.parseInt(metadata.get(name));
		}
		catch(Exception e) {
			return 0;
		}
	}
	
	public boolean isDirectory() {
		return "true".equals(this.metadata.get("isfolder"));
	}
	
	public  boolean isFile() {
		return !this.isDirectory();
	}
	
	public static CloudStorageAsset newAsset(String id, String name) {
		CloudStorageAsset asset = new CloudStorageAsset();
		asset.id = id;
		asset.name = name;
		
		return asset;
	}
	
	public static CloudStorageAsset newFileAsset(String id, String name) {
		CloudStorageAsset asset = newAsset(id, name);
		asset.metadata.put("isfolder", "false");
		
		return asset;
	}
	
	public static CloudStorageAsset newDirectoryAsset(String id, String name) {
		CloudStorageAsset asset = newAsset(id, name);
		asset.metadata.put("isfolder", "true");
		
		return asset;
	}
}

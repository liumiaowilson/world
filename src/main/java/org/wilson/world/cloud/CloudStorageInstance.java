package org.wilson.world.cloud;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.manager.CloudStorageManager;
import org.wilson.world.manager.StorageManager;
import org.wilson.world.model.CloudStorageData;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageProvider;
import org.wilson.world.storage.StorageStatus;

import net.sf.json.JSONObject;

public class CloudStorageInstance implements StorageProvider {
	private int id;
	private String name;
	private CloudStorageService service;
	
	private Map<Integer, StorageAsset> assets = new HashMap<Integer, StorageAsset>();
	private StorageStatus status = StorageStatus.Unknown;
	
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
		
		service = service.cloneService();
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
	
	private StorageAsset addCloudStorageAsset(CloudStorageAsset asset, int id) {
		if(asset != null) {
			StorageAsset storageAsset = new StorageAsset();
			storageAsset.id = id;
			storageAsset.name = asset.name;
			this.assets.put(storageAsset.id, storageAsset);
			return storageAsset;
		}
		
		return null;
	}
	
	@Override
	public int sync(int start) throws Exception {
		this.service.sync();
		
		this.assets.clear();
		List<CloudStorageAsset> assets = this.service.getCloudStorageAssets();
		int i = start;
		for(CloudStorageAsset asset : assets) {
			if(asset.isFile()) {
				this.addCloudStorageAsset(asset, i);
				i++;
			}
		}
		
		double used_pct = this.service.getUsedPercentage();
		this.status = StorageManager.getInstance().toStorageStatus(used_pct);
		
		return this.assets.size();
	}

	@Override
	public Map<Integer, StorageAsset> getStorageAssets() {
		return this.assets;
	}

	@Override
	public StorageAsset getStorageAsset(int id) {
		return this.assets.get(id);
	}

	@Override
	public StorageAsset getStorageAsset(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(StorageAsset asset : this.assets.values()) {
			if(name.equals(asset.name)) {
				return asset;
			}
		}
		
		return null;
	}

	@Override
	public StorageStatus getStorageStatus() {
		return this.status;
	}

	@Override
	public StorageAsset createStorageAsset(String name, String url, int assetId) throws Exception {
		if(StringUtils.isBlank(name) || StringUtils.isBlank(url)) {
			return null;
		}
		
		URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.connect();
        
        CloudStorageAsset asset = this.service.createCloudStorageAsset(name, connection.getInputStream());
        if(asset != null) {
        	return this.addCloudStorageAsset(asset, assetId);
        }
		
		return null;
	}

	@Override
	public String deleteStorageAsset(StorageAsset asset) throws Exception {
		if(asset == null) {
			return "No storage asset is passed in.";
		}
		
		CloudStorageAsset cloudStorageAsset = this.toCloudStorageAsset(asset);
		if(cloudStorageAsset == null) {
			return "No cloud storage asset could be found.";
		}
		
		boolean success = this.service.deleteCloudStorageAsset(cloudStorageAsset.id);
		if(!success) {
			return "Failed to delete storage asset.";
		}
		
		this.assets.remove(asset.id);
		
		return null;
	}
	
	public CloudStorageAsset toCloudStorageAsset(StorageAsset asset) throws Exception {
		if(asset == null) {
			return null;
		}
		
		return this.service.getCloudStorageAssetByName(asset.name);
	}

	@Override
	public String getImageUrl(StorageAsset asset) throws Exception {
		return this.getImageUrl(asset, -1, -1, true);
	}

	@Override
	public String getImageUrl(StorageAsset asset, int width, int height, boolean adjust) throws Exception {
		if(asset == null) {
			return null;
		}
		
		CloudStorageAsset cloudStorageAsset = this.toCloudStorageAsset(asset);
		if(cloudStorageAsset == null) {
			return null;
		}
		
		return this.service.getImageUrl(cloudStorageAsset, width, height, adjust);
	}

	@Override
	public String getContent(StorageAsset asset) throws Exception {
		if(asset == null) {
			return null;
		}
		
		CloudStorageAsset cloudStorageAsset = this.toCloudStorageAsset(asset);
		if(cloudStorageAsset == null) {
			return null;
		}
		
		return this.service.getContent(cloudStorageAsset);
	}

	@Override
	public double getUsedPercentage() throws Exception {
		return this.service.getUsedPercentage();
	}

	@Override
	public boolean prefer(StorageAsset asset) throws Exception {
		if(asset == null) {
			return false;
		}
		
		String name = asset.name;
		int pos = name.lastIndexOf("/");
		if(pos < 0) {
			return false;
		}
		
		String path = name.substring(0, pos);
		return this.service.hasPath(path);
	}
}

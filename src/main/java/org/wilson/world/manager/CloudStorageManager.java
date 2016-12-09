package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cloud.CloudStorageInstance;
import org.wilson.world.cloud.CloudStorageService;
import org.wilson.world.cloud.DefaultCloudStorageService;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.CloudStorageData;

public class CloudStorageManager implements JavaExtensionListener<CloudStorageService>, ManagerLifecycle {
	private static CloudStorageManager instance;
	
	private Map<String, CloudStorageService> services = new HashMap<String, CloudStorageService>();
	private Map<String, CloudStorageInstance> instances = new HashMap<String, CloudStorageInstance>();
	
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
	
	private void addCloudStorageInstance(CloudStorageData data) {
		CloudStorageInstance instance = new CloudStorageInstance(data);
		instances.put(instance.getName(), instance);
		
		StorageManager.getInstance().addStorageProvider(instance);
	}
	
	private void removeCloudStorageInstance(CloudStorageData data) {
		CloudStorageInstance instance = instances.remove(data.name);
		
		StorageManager.getInstance().removeStorageProvider(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		CachedDAO<CloudStorageData> dao = (CachedDAO<CloudStorageData>) CloudStorageDataManager.getInstance().getDAO();
		Cache<Integer, CloudStorageData> cache = dao.getCache();
		cache.addCacheListener(new CacheListener<CloudStorageData>(){

			@Override
			public void cachePut(CloudStorageData old, CloudStorageData v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				addCloudStorageInstance(v);
			}

			@Override
			public void cacheDeleted(CloudStorageData v) {
				removeCloudStorageInstance(v);
			}

			@Override
			public void cacheLoaded(List<CloudStorageData> all) {
				for(CloudStorageData data : all) {
					cachePut(null, data);
				}
			}

			@Override
			public void cacheLoading(List<CloudStorageData> old) {
				instances.clear();
			}
			
		});
		
		cache.notifyLoaded();
	}

	@Override
	public void shutdown() {
	}
	
	public List<CloudStorageInstance> getCloudStorageInstances() {
		return new ArrayList<CloudStorageInstance>(this.instances.values());
	}
	
	public CloudStorageInstance getCloudStorageInstance(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.instances.get(name);
	}
}

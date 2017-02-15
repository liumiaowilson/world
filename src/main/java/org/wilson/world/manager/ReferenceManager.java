package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.js.JsFileReferenceProvider;
import org.wilson.world.reference.DefaultReferenceProvider;
import org.wilson.world.reference.ReferenceIterable;
import org.wilson.world.reference.ReferenceProvider;

public class ReferenceManager implements JavaExtensionListener<ReferenceProvider> {
	private static ReferenceManager instance;
	
	private Map<String, ReferenceProvider> providers = new HashMap<String, ReferenceProvider>();
	
	private Map<String, Object> references = new HashMap<String, Object>();
	
	private ReferenceManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
		
		this.loadSystemReferenceProviders();
	}

	public static ReferenceManager getInstance() {
		if(instance == null) {
			instance = new ReferenceManager();
		}
		
		return instance;
	}
	
	private void loadSystemReferenceProviders() {
		this.addReferenceProvider(new DefaultReferenceProvider());
		this.addReferenceProvider(new JsFileReferenceProvider());
	}
	
	public void addReferenceProvider(ReferenceProvider provider) {
		if(provider != null && provider.getName() != null) {
			provider.init();
			
			this.providers.put(provider.getName(), provider);
		}
	}
	
	public void removeReferenceProvider(ReferenceProvider provider) {
		if(provider != null) {
			this.providers.remove(provider.getName());
			
			provider.destroy();
		}
	}
	
	public List<ReferenceProvider> getReferenceProviders() {
		return new ArrayList<ReferenceProvider>(this.providers.values());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getReferences(String key) {
		List ret = new ArrayList();
		
		if(StringUtils.isNotBlank(key)) {
			for(ReferenceProvider provider : this.providers.values()) {
				Object value = provider.getReference(key);
				if(value != null) {
					ret.add(value);
				}
			}
		}
		
		return ret;
	}
	
	public Object getReference(String name, String key) {
		if(StringUtils.isBlank(name) || StringUtils.isBlank(key)) {
			return null;
		}
		
		ReferenceProvider provider = this.providers.get(name);
		if(provider != null) {
			return provider.getReference(key);
		}
		
		return null;
	}
	
	public List<String> getReferenceKeys(String name) {
		List<String> ret = new ArrayList<String>();
		
		if(StringUtils.isBlank(name)) {
			return ret;
		}
		
		ReferenceProvider provider = this.providers.get(name);
		if(provider == null) {
			return ret;
		}
		
		if(provider instanceof ReferenceIterable) {
			ReferenceIterable iter = (ReferenceIterable) provider;
			return iter.getKeys();
		}
		
		return ret;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getReferenceValues(String name) {
		List ret = new ArrayList();
		for(String key : this.getReferenceKeys(name)) {
			Object ref = this.getReference(name, key);
			ret.add(ref);
		}
		
		return ret;
	}
	
	public Object getDefaultReference(String key) {
		return this.references.get(key);
	}
	
	public void setDefaultReference(String key, Object value) {
		this.references.put(key, value);
	}
	
	public Map<String, Object> getDefaultReferences() {
		return this.references;
	}
	
	public void clearDefaultReferences() {
		this.references.clear();
	}

	@Override
	public Class<ReferenceProvider> getExtensionClass() {
		return ReferenceProvider.class;
	}

	@Override
	public void created(ReferenceProvider t) {
		this.addReferenceProvider(t);
	}

	@Override
	public void removed(ReferenceProvider t) {
		this.removeReferenceProvider(t);
	}
}

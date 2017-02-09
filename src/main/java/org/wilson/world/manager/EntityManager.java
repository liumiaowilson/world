package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.entity.EntityDefinition;
import org.wilson.world.entity.EntityProperty;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Entity;
import org.wilson.world.model.EntityDef;

import net.sf.json.JSONObject;

public class EntityManager implements ManagerLifecycle {
	private static final Logger logger = Logger.getLogger(EntityManager.class);
	
    private static EntityManager instance;
    
    private Map<String, EntityDefinition> defs = new HashMap<String, EntityDefinition>();
    
	private EntityManager() {
    	
    }
    
    public static EntityManager getInstance() {
        if(instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }
    
    public List<EntityDefinition> getEntityDefinitions() {
    	return new ArrayList<EntityDefinition>(defs.values());
    }
    
    public EntityDefinition getEntityDefinition(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	return defs.get(name);
    }

    @SuppressWarnings("unchecked")
	@Override
	public void start() {
		CachedDAO<EntityDef> dao = (CachedDAO<EntityDef>) EntityDefManager.getInstance().getDAO();
    	dao.getCache().addCacheListener(new CacheListener<EntityDef>() {

			@Override
			public void cachePut(EntityDef old, EntityDef v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				EntityDefinition def = new EntityDefinition();
				def.id = v.id;
				def.name = v.name;
				try {
					JSONObject obj = JSONObject.fromObject(v.def);
					for(Object keyObj : obj.keySet()) {
						String key = (String)keyObj;
						JSONObject valueObj = obj.getJSONObject(key);
						EntityProperty property = new EntityProperty();
						property.name = key;
						if(valueObj.containsKey("type")) {
							property.type = valueObj.getString("type");
						}
						if(valueObj.containsKey("field")) {
							property.field = valueObj.getString("field");
						}
						if(valueObj.containsKey("index")) {
							property.index = valueObj.getBoolean("index");
						}
						def.properties.put(property.name, property);
					}
					
					defs.put(def.name, def);
				}
				catch(Exception e) {
					logger.error(e);
				}
			}

			@Override
			public void cacheDeleted(EntityDef v) {
				defs.remove(v.name);
			}

			@Override
			public void cacheLoaded(List<EntityDef> all) {
				for(EntityDef def : all) {
					cachePut(null, def);
				}
			}

			@Override
			public void cacheLoading(List<EntityDef> old) {
				defs.clear();
			}
    		
    	});
    	
    	dao.getCache().notifyLoaded();
	}

	@Override
	public void shutdown() {
	}
}

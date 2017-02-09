package org.wilson.world.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.file.RemoteFile;
import org.wilson.world.manager.EntityManager;
import org.wilson.world.manager.RemoteFileManager;
import org.wilson.world.model.Entity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EntityDelegator {
	private static final Logger logger = Logger.getLogger(EntityDelegator.class);
	
	private final String type;
	private Map<String, Entity> indexEntities = new HashMap<String, Entity>();
	
	public EntityDelegator(String type) {
		this.type = type;
	}
	
	public String getEntityType() {
		return type;
	}
	
	public Entity newEntity(String name) {
		Entity ret = new Entity();
		ret.type = type;
		ret.name = name;
		
		return ret;
	}
	
	public List<Entity> getEntities() {
		return new ArrayList<Entity>(indexEntities.values());
	}
	
	public Entity getEntity(String name, boolean load) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		Entity entity = indexEntities.get(name);
		return load ? load(entity) : entity;
	}
	
	public Entity getEntity(String name) {
		return getEntity(name, false);
	}
	
	public Entity getEntity(int id, boolean load) {
		Entity ret = null;
		for(Entity entity : indexEntities.values()) {
			if(entity.id == id) {
				ret = entity;
				break;
			}
		}
		
		return load ? load(ret) : ret;
	}
	
	public Entity getEntity(int id) {
		return getEntity(id, false);
	}
	
	public Entity load(Entity entity) {
		if(entity == null) {
			return null;
		}
		
		RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(this.getEntityFileName(entity));
		if(file != null) {
			String content = file.getContent();
			EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
			if(def != null) {
				try {
					JSONObject obj = JSONObject.fromObject(content);
					return def.toEntity(obj);
				}
				catch(Exception e) {
					logger.error(e);
				}
			}
		}
		
		return entity;
	}
	
	private String getEntityFileName(Entity entity) {
		return "/" + type + "/" + entity.id + ".json";
	}
	
	public int getNextEntityId() {
		int max = 0;
		for(Entity entity : this.indexEntities.values()) {
			if(entity.id > max) {
				max = entity.id;
			}
		}
		
		return max + 1;
	}
	
	public void create(Entity entity) {
		if(entity == null) {
			return;
		}
		
		if(this.indexEntities.containsKey(entity.name)) {
			throw new DataException("Duplicated name for [" + entity.name + "]");
		}
		
		entity.id = this.getNextEntityId();
		this.indexEntities.put(entity.name, entity);
		
		this.flushEntity(entity);
		this.flushIndex();
	}
	
	public void update(Entity entity) {
		if(entity == null) {
			return;
		}
		
		this.indexEntities.put(entity.name, entity);
		
		this.flushEntity(entity);
		this.flushIndex();
	}
	
	public void delete(Entity entity) {
		if(entity == null) {
			return;
		}
		
		this.indexEntities.remove(entity.name);
		
		this.deleteEntity(entity);
		this.flushIndex();
	}
	
	private void flushIndex() {
		JSONArray array = this.fromIndexEntities();
		if(array != null) {
			ByteArrayInputStream is = new ByteArrayInputStream(array.toString().getBytes());
			try {
				String fileName = this.getIndexFileName();
				RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(fileName);
				if(file != null) {
					RemoteFileManager.getInstance().updateRemoteFile(file, is);
				}
				else {
					file = new RemoteFile();
					file.name = fileName;
					RemoteFileManager.getInstance().createRemoteFile(file, is);
				}
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}
	
	private void flushEntity(Entity entity) {
		EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
		if(def == null) {
			return;
		}
		
		JSONObject obj = def.fromEntity(entity);
		if(obj != null) {
			ByteArrayInputStream is = new ByteArrayInputStream(obj.toString().getBytes());
			try {
				String fileName = this.getEntityFileName(entity);
				RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(fileName);
				if(file != null) {
					RemoteFileManager.getInstance().updateRemoteFile(file, is);
				}
				else {
					file = new RemoteFile();
					file.name = fileName;
					RemoteFileManager.getInstance().createRemoteFile(file, is);
				}
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}
	
	private void deleteEntity(Entity entity) {
		String fileName = this.getEntityFileName(entity);
		RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(fileName);
		if(file != null) {
			RemoteFileManager.getInstance().deleteRemoteFile(file.id);
		}
	}
	
	private String getIndexFileName() {
		return "/Entity/" + type + ".json";
	}
	
	/**
	 * Load the index
	 * 
	 */
	public void load() {
		RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(this.getIndexFileName());
		if(file != null) {
			//add entities
			String content = file.getContent();
			for(Entity entity : toIndexEntities(content)) {
				this.indexEntities.put(entity.name, entity);
			}
		}

		logger.info("Loaded entity [" + type + "]: " + this.indexEntities.size());
	}
	
	private JSONArray fromIndexEntities() {
		JSONArray array = new JSONArray();
		EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
		if(def != null) {
			for(Entity entity : this.indexEntities.values()) {
				JSONObject obj = def.fromIndexEntity(entity);
				array.add(obj);
			}
		}
		
		return array;
	}
	
    private List<Entity> toIndexEntities(String content) {
    	List<Entity> ret = new ArrayList<Entity>();
    	
    	try {
    		EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
    		if(def == null) {
    			return ret;
    		}
    		
    		JSONArray array = JSONArray.fromObject(content);
    		for(int i = 0; i < array.size(); i++) {
    			JSONObject obj = array.getJSONObject(i);
    			Entity entity = def.toIndexEntity(obj);
    			
    			ret.add(entity);
    		}
    	}
    	catch(Exception e) {
    		logger.error(e);
    	}
    	
    	return ret;
    }
}

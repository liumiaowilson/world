package org.wilson.world.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.file.RemoteFile;
import org.wilson.world.file.RemoteFileListener;
import org.wilson.world.manager.EntityManager;
import org.wilson.world.manager.RemoteFileManager;
import org.wilson.world.model.Entity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EntityDelegator implements RemoteFileListener {
	private static final Logger logger = Logger.getLogger(EntityDelegator.class);
	
	private final String type;
	private Map<String, Entity> indexEntities = new HashMap<String, Entity>();

    private Map<String, Entity> backupEntities = new HashMap<String, Entity>();
    private Map<String, Entity> cachedEntities = new HashMap<String, Entity>();
	
	private List<EntityListener> listeners = new ArrayList<EntityListener>();
	
	public EntityDelegator(String type) {
		this.type = type;
	}
	
	public void addEntityListener(EntityListener listener) {
		if(listener != null) {
			this.listeners.add(listener);
		}
	}
	
	public void removeEntityListener(EntityListener listener) {
		if(listener != null) {
			this.listeners.remove(listener);
		}
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

    public Entity cloneEntity(Entity entity) {
        if(entity == null) {
            return null;
        }

        Entity cloned = new Entity();
        cloned.id = entity.id;
        cloned.name = entity.name;
        cloned.type = entity.type;
        cloned.data = new HashMap<String, String>();
        for(Entry<String, String> entry : entity.data.entrySet()) {
            cloned.data.put(entry.getKey(), entry.getValue());
        }

        return cloned;
    }

    public Map<String, Entity> getBackupEntities() {
        return this.backupEntities;
    }

    public Map<String, Entity> getCachedEntities() {
        return this.cachedEntities;
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
		
        Entity cachedEntity = this.cachedEntities.get(entity.name);
        if(cachedEntity == null) {
            RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(this.getEntityFileName(entity));
            if(file != null) {
                String content = file.getContent();
                EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
                if(def != null) {
                    try {
                        JSONObject obj = JSONObject.fromObject(content);
                        cachedEntity = def.toEntity(obj);
                        this.cachedEntities.put(entity.name, cachedEntity);
                    }
                    catch(Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
		
		return cloneEntity(cachedEntity);
	}
	
	private String getIndexFileName() {
		return "/Entity/_index/" + type + ".json";
	}
	
	private String getEntityFileName(Entity entity) {
		return "/Entity/" + type + "/" + entity.id + ".json";
	}
	
	private Entity toEntity(RemoteFile file) {
		String name = file.name;
		String prefix = "/Entity/" + type + "/";
		String suffix = ".json";
		if(!name.startsWith(prefix) || !name.endsWith(suffix)) {
			return null;
		}
		
		String idStr = name.substring(prefix.length(), name.length() - suffix.length());
		try {
			int id = Integer.parseInt(idStr);
			return this.getEntity(id);
		}
		catch(Exception e) {
		}
		
		return null;
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
        this.cachedEntities.put(entity.name, entity);
		
		this.flushEntity(entity);
		this.flushIndex();
	}
	
	public void update(Entity entity) {
		if(entity == null) {
			return;
		}

        Entity oldEntity = this.getEntity(entity.id);
        if(oldEntity != null) {
            this.indexEntities.remove(oldEntity.name);
            oldEntity = this.cachedEntities.remove(oldEntity.name);
            if(oldEntity != null) {
                this.backupEntities.put(oldEntity.name, oldEntity);
            }
        }
		
		this.indexEntities.put(entity.name, entity);
        this.cachedEntities.put(entity.name, entity);
		
		this.flushEntity(entity);
		this.flushIndex();
	}
	
	public void delete(Entity entity) {
		if(entity == null) {
			return;
		}
		
		this.indexEntities.remove(entity.name);
        Entity oldEntity = this.cachedEntities.remove(entity.name);
        if(oldEntity != null) {
            this.backupEntities.put(oldEntity.name, oldEntity);
        }
		
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
	
	/**
	 * Load the index
	 * 
	 */
	public void load() {
        this.indexEntities.clear();

		RemoteFile file = RemoteFileManager.getInstance().getRemoteFile(this.getIndexFileName());
		if(file != null) {
			//add entities
			String content = file.getContent();
			for(Entity entity : toIndexEntities(content)) {
				this.indexEntities.put(entity.name, entity);
			}
		}
		
		List<Entity> entities = new ArrayList<Entity>(this.indexEntities.values());
		for(EntityListener listener : listeners) {
			listener.reloaded(entities);
		}
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

	@Override
	public void created(RemoteFile file) {
		Entity entity = this.toEntity(file);
		if(entity != null) {
			for(EntityListener listener : this.listeners) {
				listener.created(entity);
			}
		}
	}

	@Override
	public void deleted(RemoteFile file) {
		Entity entity = this.toEntity(file);
		if(entity != null) {
			for(EntityListener listener : this.listeners) {
				listener.removed(entity);
			}
		}
	}

	@Override
	public void reloaded(List<RemoteFile> files) {
		this.load();
	}
}

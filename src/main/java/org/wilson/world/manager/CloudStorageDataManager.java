package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CloudStorageData;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class CloudStorageDataManager implements ItemTypeProvider {
    public static final String NAME = "cloud_storage_data";
    
    private static CloudStorageDataManager instance;
    
    private DAO<CloudStorageData> dao = null;
    
    @SuppressWarnings("unchecked")
    private CloudStorageDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(CloudStorageData.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(CloudStorageData data : getCloudStorageDatas()) {
                    boolean found = data.name.contains(text) || data.service.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = data.id;
                        content.name = data.name;
                        content.description = data.service;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static CloudStorageDataManager getInstance() {
        if(instance == null) {
            instance = new CloudStorageDataManager();
        }
        return instance;
    }
    
    public void createCloudStorageData(CloudStorageData data) {
        ItemManager.getInstance().checkDuplicate(data);
        
        this.dao.create(data);
    }
    
    public CloudStorageData getCloudStorageData(int id) {
    	CloudStorageData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<CloudStorageData> getCloudStorageDatas() {
        List<CloudStorageData> result = new ArrayList<CloudStorageData>();
        for(CloudStorageData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateCloudStorageData(CloudStorageData data) {
        this.dao.update(data);
    }
    
    public void deleteCloudStorageData(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof CloudStorageData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        CloudStorageData data = (CloudStorageData)target;
        return String.valueOf(data.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        CloudStorageData data = (CloudStorageData)target;
        return data.name;
    }
}

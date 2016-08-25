package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.UserItemData;
import org.wilson.world.useritem.DefaultUserItem;
import org.wilson.world.useritem.UserItem;
import org.wilson.world.useritem.UserItemEffect;
import org.wilson.world.useritem.UserItemFactory;
import org.wilson.world.useritem.UserItemMissionRewardGenerator;
import org.wilson.world.useritem.UserItemType;

public class UserItemDataManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(UserItemDataManager.class);
    
    public static final String NAME = "user_item_data";
    
    private static UserItemDataManager instance;
    
    private DAO<UserItemData> dao = null;
    
    private Cache<Integer, UserItem> cache = null;
    private Cache<String, UserItem> nameCache = null;
    
    private static int GLOBAL_ID = 1;
    
    @SuppressWarnings("unchecked")
    private UserItemDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(UserItemData.class);
        this.cache = new DefaultCache<Integer, UserItem>("user_item_data_manager_cache", false);
        this.nameCache = new DefaultCache<String, UserItem>("user_item_data_manager_name_cache", false);
        ((CachedDAO<UserItemData>)this.dao).getCache().addCacheListener(new CacheListener<UserItemData>(){

            @Override
            public void cachePut(UserItemData old, UserItemData v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                loadUserItemData(v);
            }

            @Override
            public void cacheDeleted(UserItemData v) {
                UserItemDataManager.this.cache.delete(v.id);
                UserItemDataManager.this.nameCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<UserItemData> all) {
                loadSystemUserItems();
            }

            @Override
            public void cacheLoading(List<UserItemData> old) {
                UserItemDataManager.this.cache.clear();
                UserItemDataManager.this.nameCache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        MissionManager.getInstance().addMissionRewardGenerator(new UserItemMissionRewardGenerator());
    }
    
    private void loadSystemUserItems() {
        GLOBAL_ID = 1;
        
        for(UserItem item : UserItemFactory.getInstance().getUserItems()) {
            this.loadSystemUserItem(item);
        }
    }
    
    private void loadSystemUserItem(UserItem item) {
        if(item != null) {
            item.setId(-GLOBAL_ID++);
            this.cache.put(item.getId(), item);
            this.nameCache.put(item.getName(), item);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private void loadUserItemData(UserItemData data) {
        if(data != null) {
            UserItemEffect eff = null;
            String effect = data.effect;
            if(!StringUtils.isBlank(effect)) {
                try {
                    Class clazz = Class.forName(effect);
                    eff = (UserItemEffect) clazz.newInstance();
                    logger.info("Loaded user item data using class [" + effect + "]");
                }
                catch(Exception e) {
                    eff = (UserItemEffect) ExtManager.getInstance().wrapAction(effect, UserItemEffect.class);
                    if(eff != null) {
                        logger.info("Loaded user item data using action [" + effect + "]");
                    }
                    else {
                        logger.warn("Failed to load user item data using [" + effect + "]");
                        return;
                    }
                }
            }
            if(eff != null) {
                DefaultUserItem item = new DefaultUserItem(data, eff);
                this.cache.put(item.getId(), item);
                this.nameCache.put(item.getName(), item);
            }
        }
    }
    
    public static UserItemDataManager getInstance() {
        if(instance == null) {
            instance = new UserItemDataManager();
        }
        return instance;
    }
    
    public void createUserItemData(UserItemData data) {
        ItemManager.getInstance().checkDuplicate(data);
        
        this.dao.create(data);
    }
    
    public UserItemData getUserItemData(int id) {
        UserItemData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<UserItemData> getUserItemDatas() {
        List<UserItemData> result = new ArrayList<UserItemData>();
        for(UserItemData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateUserItemData(UserItemData data) {
        this.dao.update(data);
    }
    
    public void deleteUserItemData(int id) {
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
        return target instanceof UserItemData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        UserItemData data = (UserItemData)target;
        return String.valueOf(data.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<String> getUserItemTypes() {
        List<String> ret = new ArrayList<String>();
        
        for(UserItemType type : UserItemType.values()) {
            ret.add(type.name());
        }
        
        return ret;
    }
    
    public List<UserItem> getUserItems() {
        return this.cache.getAll();
    }
    
    public UserItem getUserItem(int id) {
        return this.cache.get(id);
    }
    
    public UserItem randomUserItem() {
        List<UserItem> items = this.getUserItems();
        if(items.isEmpty()) {
            return null;
        }
        else {
            int n = DiceManager.getInstance().random(items.size());
            return items.get(n);
        }
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        UserItemData data = (UserItemData)target;
        return data.name;
    }
}

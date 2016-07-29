package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.UserItemData;

public class UserItemDataManager implements ItemTypeProvider {
    public static final String NAME = "user_item_data";
    
    private static UserItemDataManager instance;
    
    private DAO<UserItemData> dao = null;
    
    @SuppressWarnings("unchecked")
    private UserItemDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(UserItemData.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static UserItemDataManager getInstance() {
        if(instance == null) {
            instance = new UserItemDataManager();
        }
        return instance;
    }
    
    public void createUserItemData(UserItemData data) {
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

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

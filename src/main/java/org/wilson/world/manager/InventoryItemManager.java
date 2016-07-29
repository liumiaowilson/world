package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.InventoryItem;
import org.wilson.world.useritem.UserItem;
import org.wilson.world.useritem.UserItemStatus;

public class InventoryItemManager implements ItemTypeProvider {
    public static final String NAME = "inv_item";
    
    private static InventoryItemManager instance;
    
    private DAO<InventoryItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private InventoryItemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(InventoryItem.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static InventoryItemManager getInstance() {
        if(instance == null) {
            instance = new InventoryItemManager();
        }
        return instance;
    }
    
    public void createInventoryItem(InventoryItem item) {
        this.dao.create(item);
    }
    
    public InventoryItem getInventoryItem(int id) {
        InventoryItem item = this.dao.get(id);
        if(item != null) {
            UserItem userItem = UserItemDataManager.getInstance().getUserItem(item.itemId);
            if(userItem == null) {
                item = null;
            }
            else {
                item.name = userItem.getName();
                item.type = userItem.getType();
            }
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> result = new ArrayList<InventoryItem>();
        for(InventoryItem item : this.dao.getAll()) {
            UserItem userItem = UserItemDataManager.getInstance().getUserItem(item.itemId);
            if(userItem != null) {
                item.name = userItem.getName();
                item.type = userItem.getType();
                result.add(item);
            }
        }
        return result;
    }
    
    public void updateInventoryItem(InventoryItem item) {
        this.dao.update(item);
    }
    
    public void deleteInventoryItem(int id) {
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
        return target instanceof InventoryItem;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        InventoryItem item = (InventoryItem)target;
        return String.valueOf(item.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public InventoryItem gamble() {
        UserItem userItem = UserItemDataManager.getInstance().randomUserItem();
        if(userItem == null) {
            return null;
        }
        InventoryItem item = new InventoryItem();
        item.itemId = userItem.getId();
        item.name = userItem.getName();
        item.type = userItem.getType();
        item.price = 0;
        item.amount = 1;
        item.status = UserItemStatus.READY.name();
        this.createInventoryItem(item);
        
        return item;
    }
}

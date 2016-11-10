package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.InventoryItem;
import org.wilson.world.useritem.InventoryItemDBCleaner;
import org.wilson.world.useritem.RandomUserItemEventListener;
import org.wilson.world.useritem.UserItem;
import org.wilson.world.useritem.UserItemFactory;
import org.wilson.world.useritem.UserItemStatus;
import org.wilson.world.useritem.UserItemType;

public class InventoryItemManager implements ItemTypeProvider {
    public static final String NAME = "inv_item";
    
    private static InventoryItemManager instance;
    
    private DAO<InventoryItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private InventoryItemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(InventoryItem.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new InventoryItemDBCleaner());
        
        EventManager.getInstance().registerListener(EventType.GainExperience, new RandomUserItemEventListener());
    }
    
    public static InventoryItemManager getInstance() {
        if(instance == null) {
            instance = new InventoryItemManager();
        }
        return instance;
    }
    
    public void createInventoryItem(InventoryItem item) {
        ItemManager.getInstance().checkDuplicate(item);
        
        this.dao.create(item);
    }
    
    public InventoryItem getInventoryItem(int id) {
        InventoryItem item = this.dao.get(id);
        if(item != null) {
            this.loadInventoryItem(item);
            return item;
        }
        else {
            return null;
        }
    }
    
    public InventoryItem getInventoryItem(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(InventoryItem item : this.getInventoryItems()) {
            if(item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    private void loadInventoryItem(InventoryItem item) {
        if(item == null) {
            return;
        }
        
        UserItem userItem = UserItemDataManager.getInstance().getUserItem(item.itemId);
        if(userItem != null) {
            item.name = userItem.getName();
            item.type = userItem.getType();
            item.description = userItem.getDescription();
        }
    }
    
    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> result = new ArrayList<InventoryItem>();
        for(InventoryItem item : this.dao.getAll()) {
            this.loadInventoryItem(item);
            result.add(item);
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public InventoryItem search() {
        if(DiceManager.getInstance().dice(5)) {
            UserItem userItem = UserItemDataManager.getInstance().randomUserItem();
            if(userItem == null) {
                return null;
            }
            return this.addUserItem(userItem);
        }
        else {
            return null;
        }
    }
    
    public InventoryItem addUserItem(UserItem userItem) {
        return this.addUserItem(userItem, 1);
    }
    
    public InventoryItem addUserItem(UserItem userItem, int amount) {
        if(userItem == null) {
            return null;
        }
        InventoryItem item = new InventoryItem();
        item.itemId = userItem.getId();
        item.name = userItem.getName();
        item.type = userItem.getType();
        item.price = 0;
        item.amount = amount;
        item.status = UserItemStatus.READY.name();
        this.addInventoryItem(item);
        
        return item;
    }
    
    public List<InventoryItem> getInventoryItemsByUserItemId(int id) {
        List<InventoryItem> ret = new ArrayList<InventoryItem>();
        
        for(InventoryItem item : this.getInventoryItems()) {
            if(item.itemId == id) {
                ret.add(item);
            }
        }
        
        return ret;
    }
    
    public InventoryItem getInventoryItem(int userItemId, String status) {
        for(InventoryItem item : this.getInventoryItems()) {
            if(item.itemId == userItemId && item.status.equals(status)) {
                return item;
            }
        }
        
        return null;
    }
    
    public void addInventoryItem(InventoryItem item) {
        if(item == null) {
            return;
        }
        
        InventoryItem oldItem = this.getInventoryItem(item.itemId, item.status);
        if(oldItem == null) {
            this.createInventoryItem(item);
        }
        else {
            int amount = oldItem.amount + item.amount;
            int price = (oldItem.price * oldItem.amount + item.price * item.amount) / amount;
            oldItem.price = price;
            oldItem.amount = amount;
            this.updateInventoryItem(oldItem);
        }
    }
    
    public void removeInventoryItem(InventoryItem item) {
        if(item == null) {
            return;
        }
        
        InventoryItem oldItem = this.getInventoryItem(item.itemId, item.status);
        if(oldItem == null) {
            return;
        }
        else {
            int amount = oldItem.amount - item.amount;
            if(amount <= 0) {
                this.deleteInventoryItem(oldItem.id);
            }
            else {
                int price = (oldItem.price * oldItem.amount - item.price * item.amount) / amount;
                if(price < 0) {
                    price = 0;
                }
                oldItem.price = price;
                oldItem.amount = amount;
                this.updateInventoryItem(oldItem);
            }
        }
    }
    
    public String useInventoryItem(int invItemId) {
        InventoryItem item = this.getInventoryItem(invItemId);
        if(item == null) {
            return "No inventory item found";
        }
        if(!UserItemStatus.READY.name().equals(item.status)) {
            return "Inventory item is not read";
        }
        UserItem userItem = UserItemDataManager.getInstance().getUserItem(item.itemId);
        if(userItem == null) {
            return "Invalid inventory item found";
        }
        
        if(UserItemType.Potion.name().equals(userItem.getType())) {
            userItem.takeEffect();
            this.consumeInventoryItem(item);
        }
        else {
            userItem.takeEffect();
        }
        
        return null;
    }
    
    public boolean readGalleryTicket() {
        return this.useGalleryTicket(1) == null;
    }
    
    public String useGalleryTicket(int amount) {
    	InventoryItem item = this.getInventoryItem(UserItemFactory.GALLERY_TICKET_NAME);
        if(item == null) {
            return "No gallery ticket found";
        }
        
        if(item.amount < amount) {
            return "No enough gallery tickets";
        }
        
        this.consumeInventoryItem(item, amount);
        
        return null;
    }
    
    public void consumeInventoryItem(InventoryItem item, int amount) {
        if(item == null || amount <= 0) {
            return;
        }
        item.amount -= amount;
        if(item.amount <= 0) {
            this.deleteInventoryItem(item.id);
        }
        else {
            this.updateInventoryItem(item);
        }
    }
    
    public void consumeInventoryItem(InventoryItem item) {
        this.consumeInventoryItem(item, 1);
    }
    
    @Override
    public String getIdentifier(Object target) {
        return null;
    }
}

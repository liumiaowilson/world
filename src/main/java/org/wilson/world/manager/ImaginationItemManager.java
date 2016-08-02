package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ImaginationItem;

public class ImaginationItemManager implements ItemTypeProvider {
    public static final String NAME = "imagination_item";
    
    private static ImaginationItemManager instance;
    
    private DAO<ImaginationItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private ImaginationItemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ImaginationItem.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ImaginationItemManager getInstance() {
        if(instance == null) {
            instance = new ImaginationItemManager();
        }
        return instance;
    }
    
    public void createImaginationItem(ImaginationItem item) {
        this.dao.create(item);
    }
    
    public ImaginationItem getImaginationItem(int id) {
        ImaginationItem item = this.dao.get(id);
        if(item != null) {
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<ImaginationItem> getImaginationItems() {
        List<ImaginationItem> result = new ArrayList<ImaginationItem>();
        for(ImaginationItem item : this.dao.getAll()) {
            result.add(item);
        }
        return result;
    }
    
    public void updateImaginationItem(ImaginationItem item) {
        this.dao.update(item);
    }
    
    public void deleteImaginationItem(int id) {
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
        return target instanceof ImaginationItem;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ImaginationItem item = (ImaginationItem)target;
        return String.valueOf(item.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public ImaginationItem randomImaginationItem() {
        List<ImaginationItem> items = this.getImaginationItems();
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }
}

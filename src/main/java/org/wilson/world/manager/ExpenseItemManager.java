package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ExpenseItem;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ExpenseItemManager implements ItemTypeProvider {
    public static final String NAME = "expense_item";
    
    private static ExpenseItemManager instance;
    
    private DAO<ExpenseItem> dao = null;
    
    private List<String> types = new ArrayList<String>();
    
    private ExpenseItem last = null;
    
    @SuppressWarnings("unchecked")
    private ExpenseItemManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ExpenseItem.class);
        
        this.loadTypes();
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ExpenseItem item : getExpenseItems()) {
                    boolean found = item.name.contains(text) || item.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = item.id;
                        content.name = item.name;
                        content.description = item.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ExpenseItemManager getInstance() {
        if(instance == null) {
            instance = new ExpenseItemManager();
        }
        return instance;
    }
    
    private void loadTypes() {
        String types = ConfigManager.getInstance().getConfig("expense.types", "Food,Clothes,Travel,Gift,Other");
        for(String type : types.split(",")) {
            this.types.add(type);
        }
        
        Collections.sort(this.types);
        if(this.types.contains("Other")) {
            this.types.remove("Other");
            this.types.add("Other");
        }
    }
    
    public void createExpenseItem(ExpenseItem item) {
        this.dao.create(item);
        
        this.last = item;
    }
    
    public ExpenseItem getExpenseItem(int id) {
        ExpenseItem item = this.dao.get(id);
        if(item != null) {
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<ExpenseItem> getExpenseItems() {
        List<ExpenseItem> result = new ArrayList<ExpenseItem>();
        for(ExpenseItem item : this.dao.getAll()) {
            result.add(item);
        }
        return result;
    }
    
    public void updateExpenseItem(ExpenseItem item) {
        this.dao.update(item);
    }
    
    public void deleteExpenseItem(int id) {
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
        return target instanceof ExpenseItem;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ExpenseItem item = (ExpenseItem)target;
        return String.valueOf(item.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<String> getTypes() {
        return this.types;
    }
    
    public ExpenseItem getLastCreatedExpenseItem() {
        return this.last;
    }
}

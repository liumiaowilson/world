package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.FlashCardSet;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class FlashCardSetManager implements ItemTypeProvider {
    public static final String NAME = "flashcard_set";
    
    private static FlashCardSetManager instance;
    
    private DAO<FlashCardSet> dao = null;
    
    @SuppressWarnings("unchecked")
    private FlashCardSetManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(FlashCardSet.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(FlashCardSet set : getFlashCardSets()) {
                    boolean found = set.name.contains(text) || set.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = set.id;
                        content.name = set.name;
                        content.description = set.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FlashCardSetManager getInstance() {
        if(instance == null) {
            instance = new FlashCardSetManager();
        }
        return instance;
    }
    
    public void createFlashCardSet(FlashCardSet set) {
        ItemManager.getInstance().checkDuplicate(set);
        
        this.dao.create(set);
    }
    
    public FlashCardSet getFlashCardSet(int id) {
        FlashCardSet set = this.dao.get(id);
        if(set != null) {
            return set;
        }
        else {
            return null;
        }
    }
    
    public List<FlashCardSet> getFlashCardSets() {
        List<FlashCardSet> result = new ArrayList<FlashCardSet>();
        for(FlashCardSet set : this.dao.getAll()) {
            result.add(set);
        }
        return result;
    }
    
    public void updateFlashCardSet(FlashCardSet set) {
        this.dao.update(set);
    }
    
    public void deleteFlashCardSet(int id) {
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
        return target instanceof FlashCardSet;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        FlashCardSet set = (FlashCardSet)target;
        return String.valueOf(set.id);
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
        
        FlashCardSet set = (FlashCardSet)target;
        return set.name;
    }
}

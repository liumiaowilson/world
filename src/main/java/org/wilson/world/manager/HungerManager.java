package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.hunger.HungerIdeaConverter;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Hunger;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class HungerManager implements ItemTypeProvider {
    public static final String NAME = "hunger";
    
    private static HungerManager instance;
    
    private DAO<Hunger> dao = null;
    
    @SuppressWarnings("unchecked")
    private HungerManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Hunger.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        IdeaConverterFactory.getInstance().addIdeaConverter(new HungerIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Hunger hunger : getHungers()) {
                    boolean found = hunger.name.contains(text) || hunger.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = hunger.id;
                        content.name = hunger.name;
                        content.description = hunger.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static HungerManager getInstance() {
        if(instance == null) {
            instance = new HungerManager();
        }
        return instance;
    }
    
    public void createHunger(Hunger hunger) {
        ItemManager.getInstance().checkDuplicate(hunger);
        
        this.dao.create(hunger);
    }
    
    public Hunger getHunger(int id) {
    	Hunger hunger = this.dao.get(id);
        if(hunger != null) {
            return hunger;
        }
        else {
            return null;
        }
    }
    
    public List<Hunger> getHungers() {
        List<Hunger> result = new ArrayList<Hunger>();
        for(Hunger hunger : this.dao.getAll()) {
            result.add(hunger);
        }
        return result;
    }
    
    public void updateHunger(Hunger hunger) {
        this.dao.update(hunger);
    }
    
    public void deleteHunger(int id) {
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
        return target instanceof Hunger;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Hunger hunger = (Hunger)target;
        return String.valueOf(hunger.id);
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
        
        Hunger hunger = (Hunger)target;
        return hunger.name;
    }
    
    public Hunger randomHunger() {
    	List<Hunger> hungers = this.getHungers();
    	if(hungers.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(hungers.size());
    	return hungers.get(n);
    }
}

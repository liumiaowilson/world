package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Spice;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class SpiceManager implements ItemTypeProvider {
    public static final String NAME = "spice";
    
    private static SpiceManager instance;
    
    private DAO<Spice> dao = null;
    
    @SuppressWarnings("unchecked")
    private SpiceManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Spice.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Spice spice : getSpices()) {
                    boolean found = spice.name.contains(text) || spice.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = spice.id;
                        content.name = spice.name;
                        content.description = spice.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static SpiceManager getInstance() {
        if(instance == null) {
            instance = new SpiceManager();
        }
        return instance;
    }
    
    public void createSpice(Spice spice) {
        ItemManager.getInstance().checkDuplicate(spice);
        
        this.dao.create(spice);
    }
    
    public Spice getSpice(int id) {
        Spice spice = this.dao.get(id);
        if(spice != null) {
            return spice;
        }
        else {
            return null;
        }
    }
    
    public List<Spice> getSpices() {
        List<Spice> result = new ArrayList<Spice>();
        for(Spice spice : this.dao.getAll()) {
            result.add(spice);
        }
        return result;
    }
    
    public void updateSpice(Spice spice) {
        this.dao.update(spice);
    }
    
    public void deleteSpice(int id) {
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
        return target instanceof Spice;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Spice spice = (Spice)target;
        return String.valueOf(spice.id);
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
        
        Spice spice = (Spice)target;
        return spice.name;
    }
    
    public Spice randomSpice() {
    	List<Spice> spices = this.getSpices();
    	if(spices.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(spices.size());
    	return spices.get(n);
    }
}

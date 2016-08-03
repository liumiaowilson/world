package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Romance;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RomanceManager implements ItemTypeProvider {
    public static final String NAME = "romance";
    
    private static RomanceManager instance;
    
    private DAO<Romance> dao = null;
    
    @SuppressWarnings("unchecked")
    private RomanceManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Romance.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Romance romance : getRomances()) {
                    boolean found = romance.name.contains(text) || romance.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = romance.id;
                        content.name = romance.name;
                        content.description = romance.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RomanceManager getInstance() {
        if(instance == null) {
            instance = new RomanceManager();
        }
        return instance;
    }
    
    public void createRomance(Romance romance) {
        this.dao.create(romance);
    }
    
    public Romance getRomance(int id) {
        Romance romance = this.dao.get(id);
        if(romance != null) {
            return romance;
        }
        else {
            return null;
        }
    }
    
    public List<Romance> getRomances() {
        List<Romance> result = new ArrayList<Romance>();
        for(Romance romance : this.dao.getAll()) {
            result.add(romance);
        }
        return result;
    }
    
    public void updateRomance(Romance romance) {
        this.dao.update(romance);
    }
    
    public void deleteRomance(int id) {
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
        return target instanceof Romance;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Romance romance = (Romance)target;
        return String.valueOf(romance.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

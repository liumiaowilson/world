package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Alias;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class AliasManager implements ItemTypeProvider {
    public static final String NAME = "alias";
    
    private static AliasManager instance;
    
    private DAO<Alias> dao = null;
    
    @SuppressWarnings("unchecked")
    private AliasManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Alias.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Alias alias : getAliases()) {
                    boolean found = alias.name.contains(text) || alias.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = alias.id;
                        content.name = alias.name;
                        content.description = alias.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AliasManager getInstance() {
        if(instance == null) {
            instance = new AliasManager();
        }
        return instance;
    }
    
    public void createAlias(Alias alias) {
        ItemManager.getInstance().checkDuplicate(alias);
        
        this.dao.create(alias);
    }
    
    public Alias getAlias(int id) {
        Alias alias = this.dao.get(id);
        if(alias != null) {
            return alias;
        }
        else {
            return null;
        }
    }
    
    public List<Alias> getAliases() {
        List<Alias> result = new ArrayList<Alias>();
        for(Alias alias : this.dao.getAll()) {
            result.add(alias);
        }
        return result;
    }
    
    public void updateAlias(Alias alias) {
        this.dao.update(alias);
    }
    
    public void deleteAlias(int id) {
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
        return target instanceof Alias;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Alias alias = (Alias)target;
        return String.valueOf(alias.id);
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
        
        Alias alias = (Alias)target;
        return alias.name;
    }
}

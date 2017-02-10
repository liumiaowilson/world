package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Pagelet;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PageletManager implements ItemTypeProvider {
    public static final String NAME = "pagelet";
    
    private static PageletManager instance;
    
    private DAO<Pagelet> dao = null;
    
    @SuppressWarnings("unchecked")
    private PageletManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Pagelet.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Pagelet pagelet : getPagelets()) {
                    boolean found = pagelet.name.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = pagelet.id;
                        content.name = pagelet.name;
                        content.description = pagelet.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PageletManager getInstance() {
        if(instance == null) {
            instance = new PageletManager();
        }
        return instance;
    }
    
    public void createPagelet(Pagelet pagelet) {
        ItemManager.getInstance().checkDuplicate(pagelet);
        
        this.dao.create(pagelet);
    }
    
    public Pagelet getPagelet(int id, boolean lazy) {
    	Pagelet pagelet = this.dao.get(id, lazy);
        if(pagelet != null) {
            return pagelet;
        }
        else {
            return null;
        }
    }
    
    public Pagelet getPagelet(int id) {
    	return getPagelet(id, true);
    }
    
    public List<Pagelet> getPagelets() {
        List<Pagelet> result = new ArrayList<Pagelet>();
        for(Pagelet pagelet : this.dao.getAll()) {
            result.add(pagelet);
        }
        return result;
    }
    
    public void updatePagelet(Pagelet pagelet) {
        this.dao.update(pagelet);
    }
    
    public void deletePagelet(int id) {
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
        return target instanceof Pagelet;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Pagelet pagelet = (Pagelet)target;
        return String.valueOf(pagelet.id);
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
        
        Pagelet pagelet = (Pagelet)target;
        return pagelet.name;
    }
}

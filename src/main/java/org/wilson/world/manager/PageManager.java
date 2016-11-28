package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Page;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PageManager implements ItemTypeProvider {
    public static final String NAME = "page";
    
    private static PageManager instance;
    
    private DAO<Page> dao = null;
    
    @SuppressWarnings("unchecked")
    private PageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Page.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Page page : getPages()) {
                    boolean found = page.name.contains(text) || page.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = page.id;
                        content.name = page.name;
                        content.description = page.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PageManager getInstance() {
        if(instance == null) {
            instance = new PageManager();
        }
        return instance;
    }
    
    public void createPage(Page page) {
        ItemManager.getInstance().checkDuplicate(page);
        
        this.dao.create(page);
    }
    
    public Page getPage(int id) {
    	Page page = this.dao.get(id);
        if(page != null) {
            return page;
        }
        else {
            return null;
        }
    }
    
    public List<Page> getPages() {
        List<Page> result = new ArrayList<Page>();
        for(Page page : this.dao.getAll()) {
            result.add(page);
        }
        return result;
    }
    
    public void updatePage(Page page) {
        this.dao.update(page);
    }
    
    public void deletePage(int id) {
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
        return target instanceof Page;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Page page = (Page)target;
        return String.valueOf(page.id);
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
        
        Page page = (Page)target;
        return page.name;
    }
    
    public String getDefaultPageContent() {
    	return "Hello $name";
    }
}

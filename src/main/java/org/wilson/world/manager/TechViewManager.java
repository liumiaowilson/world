package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TechView;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.techview.TechViewDBCleaner;

public class TechViewManager implements ItemTypeProvider {
    public static final String NAME = "tech_view";
    
    private static TechViewManager instance;
    
    private DAO<TechView> dao = null;
    
    @SuppressWarnings("unchecked")
    private TechViewManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TechView.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(TechView view : getTechViews()) {
                    boolean found = view.name.contains(text) || view.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = view.id;
                        content.name = view.name;
                        content.description = view.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        ItemManager.getInstance().addDBCleaner(new TechViewDBCleaner());
    }
    
    public static TechViewManager getInstance() {
        if(instance == null) {
            instance = new TechViewManager();
        }
        return instance;
    }
    
    public void createTechView(TechView view) {
        ItemManager.getInstance().checkDuplicate(view);
        
        this.dao.create(view);
    }
    
    public TechView getTechView(int id) {
        TechView view = this.dao.get(id);
        if(view != null) {
            return view;
        }
        else {
            return null;
        }
    }
    
    public TechView getTechView(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(TechView view : this.getTechViews()) {
            if(name.equals(view.name)) {
                return view;
            }
        }
        
        return null;
    }
    
    public List<TechView> getTechViews() {
        List<TechView> result = new ArrayList<TechView>();
        for(TechView view : this.dao.getAll()) {
            result.add(view);
        }
        return result;
    }
    
    public void updateTechView(TechView view) {
        this.dao.update(view);
    }
    
    public void deleteTechView(int id) {
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
        return target instanceof TechView;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TechView view = (TechView)target;
        return String.valueOf(view.id);
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
        
        TechView view = (TechView)target;
        return view.name;
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.detail.DetailIdeaConverter;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Detail;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class DetailManager implements ItemTypeProvider {
    public static final String NAME = "detail";
    
    private static DetailManager instance;
    
    private DAO<Detail> dao = null;
    
    @SuppressWarnings("unchecked")
    private DetailManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Detail.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new DetailIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Detail detail : getDetails()) {
                    boolean found = detail.name.contains(text) || detail.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = detail.id;
                        content.name = detail.name;
                        content.description = detail.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static DetailManager getInstance() {
        if(instance == null) {
            instance = new DetailManager();
        }
        return instance;
    }
    
    public void createDetail(Detail detail) {
        ItemManager.getInstance().checkDuplicate(detail);
        
        this.dao.create(detail);
    }
    
    public Detail getDetail(int id) {
        Detail detail = this.dao.get(id);
        if(detail != null) {
            return detail;
        }
        else {
            return null;
        }
    }
    
    public List<Detail> getDetails() {
        List<Detail> result = new ArrayList<Detail>();
        for(Detail detail : this.dao.getAll()) {
            result.add(detail);
        }
        return result;
    }
    
    public void updateDetail(Detail detail) {
        this.dao.update(detail);
    }
    
    public void deleteDetail(int id) {
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
        return target instanceof Detail;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Detail detail = (Detail)target;
        return String.valueOf(detail.id);
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
        
        Detail detail = (Detail)target;
        return detail.name;
    }
}

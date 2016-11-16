package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelStage;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelStageManager implements ItemTypeProvider {
    public static final String NAME = "novel_stage";
    
    private static NovelStageManager instance;
    
    private DAO<NovelStage> dao = null;
    
    @SuppressWarnings("unchecked")
    private NovelStageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelStage.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelStage stage : getNovelStages()) {
                    boolean found = stage.name.contains(text) || stage.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = stage.id;
                        content.name = stage.name;
                        content.description = stage.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static NovelStageManager getInstance() {
        if(instance == null) {
            instance = new NovelStageManager();
        }
        return instance;
    }
    
    public void createNovelStage(NovelStage stage) {
        ItemManager.getInstance().checkDuplicate(stage);
        
        this.dao.create(stage);
    }
    
    public NovelStage getNovelStage(int id) {
    	NovelStage stage = this.dao.get(id);
        if(stage != null) {
            return stage;
        }
        else {
            return null;
        }
    }
    
    public List<NovelStage> getNovelStages() {
        List<NovelStage> result = new ArrayList<NovelStage>();
        for(NovelStage stage : this.dao.getAll()) {
            result.add(stage);
        }
        return result;
    }
    
    public void updateNovelStage(NovelStage stage) {
        this.dao.update(stage);
    }
    
    public void deleteNovelStage(int id) {
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
        return target instanceof NovelStage;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelStage stage = (NovelStage)target;
        return String.valueOf(stage.id);
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
        
        NovelStage stage = (NovelStage)target;
        return stage.name;
    }
}

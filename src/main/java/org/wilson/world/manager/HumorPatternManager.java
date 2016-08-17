package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.humor.HumorPatternIdeaConverter;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.HumorPattern;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class HumorPatternManager implements ItemTypeProvider {
    public static final String NAME = "humor_pattern";
    
    private static HumorPatternManager instance;
    
    private DAO<HumorPattern> dao = null;
    
    @SuppressWarnings("unchecked")
    private HumorPatternManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(HumorPattern.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new HumorPatternIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(HumorPattern pattern : getHumorPatterns()) {
                    boolean found = pattern.name.contains(text) || pattern.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = pattern.id;
                        content.name = pattern.name;
                        content.description = pattern.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static HumorPatternManager getInstance() {
        if(instance == null) {
            instance = new HumorPatternManager();
        }
        return instance;
    }
    
    public void createHumorPattern(HumorPattern pattern) {
        ItemManager.getInstance().checkDuplicate(pattern);
        
        this.dao.create(pattern);
    }
    
    public HumorPattern getHumorPattern(int id) {
        HumorPattern pattern = this.dao.get(id);
        if(pattern != null) {
            return pattern;
        }
        else {
            return null;
        }
    }
    
    public List<HumorPattern> getHumorPatterns() {
        List<HumorPattern> result = new ArrayList<HumorPattern>();
        for(HumorPattern pattern : this.dao.getAll()) {
            result.add(pattern);
        }
        return result;
    }
    
    public void updateHumorPattern(HumorPattern pattern) {
        this.dao.update(pattern);
    }
    
    public void deleteHumorPattern(int id) {
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
        return target instanceof HumorPattern;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        HumorPattern pattern = (HumorPattern)target;
        return String.valueOf(pattern.id);
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
        
        HumorPattern pattern = (HumorPattern)target;
        return pattern.name;
    }
}

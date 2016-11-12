package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelVariable;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelVariableManager implements ItemTypeProvider {
    public static final String NAME = "novel_variable";
    
    private static NovelVariableManager instance;
    
    private DAO<NovelVariable> dao = null;
    
    @SuppressWarnings("unchecked")
    private NovelVariableManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelVariable.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelVariable variable : getNovelVariables()) {
                    boolean found = variable.name.contains(text) || variable.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = variable.id;
                        content.name = variable.name;
                        content.description = variable.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static NovelVariableManager getInstance() {
        if(instance == null) {
            instance = new NovelVariableManager();
        }
        return instance;
    }
    
    public void createNovelVariable(NovelVariable variable) {
        ItemManager.getInstance().checkDuplicate(variable);
        
        this.dao.create(variable);
    }
    
    public NovelVariable getNovelVariable(int id) {
    	NovelVariable variable = this.dao.get(id);
        if(variable != null) {
            return variable;
        }
        else {
            return null;
        }
    }
    
    public List<NovelVariable> getNovelVariables() {
        List<NovelVariable> result = new ArrayList<NovelVariable>();
        for(NovelVariable variable : this.dao.getAll()) {
            result.add(variable);
        }
        return result;
    }
    
    public void updateNovelVariable(NovelVariable variable) {
        this.dao.update(variable);
    }
    
    public void deleteNovelVariable(int id) {
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
        return target instanceof NovelVariable;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelVariable variable = (NovelVariable)target;
        return String.valueOf(variable.id);
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
        
        NovelVariable variable = (NovelVariable)target;
        return variable.name;
    }
}

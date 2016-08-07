package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.GoalDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class GoalDefManager implements ItemTypeProvider {
    public static final String NAME = "goal_def";
    
    private static GoalDefManager instance;
    
    private DAO<GoalDef> dao = null;
    
    @SuppressWarnings("unchecked")
    private GoalDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(GoalDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(GoalDef def : getGoalDefs()) {
                    boolean found = def.name.contains(text) || def.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = def.id;
                        content.name = def.name;
                        content.description = def.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static GoalDefManager getInstance() {
        if(instance == null) {
            instance = new GoalDefManager();
        }
        return instance;
    }
    
    public void createGoalDef(GoalDef def) {
        ItemManager.getInstance().checkDuplicate(def);
        
        this.dao.create(def);
    }
    
    public GoalDef getGoalDef(int id) {
        GoalDef def = this.dao.get(id);
        if(def != null) {
            return def;
        }
        else {
            return null;
        }
    }
    
    public List<GoalDef> getGoalDefs() {
        List<GoalDef> result = new ArrayList<GoalDef>();
        for(GoalDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public void updateGoalDef(GoalDef def) {
        this.dao.update(def);
    }
    
    public void deleteGoalDef(int id) {
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
        return target instanceof GoalDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        GoalDef def = (GoalDef)target;
        return String.valueOf(def.id);
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
        
        GoalDef def = (GoalDef)target;
        return def.name;
    }
}

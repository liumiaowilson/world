package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Behavior;

public class BehaviorManager implements ItemTypeProvider {
    public static final String NAME = "behavior";
    
    private static BehaviorManager instance;
    
    private DAO<Behavior> dao = null;
    
    @SuppressWarnings("unchecked")
    private BehaviorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Behavior.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static BehaviorManager getInstance() {
        if(instance == null) {
            instance = new BehaviorManager();
        }
        return instance;
    }
    
    public void createBehavior(Behavior behavior) {
        ItemManager.getInstance().checkDuplicate(behavior);
        
        this.dao.create(behavior);
    }
    
    public Behavior getBehavior(int id) {
        Behavior behavior = this.dao.get(id);
        if(behavior != null) {
            return behavior;
        }
        else {
            return null;
        }
    }
    
    public List<Behavior> getBehaviors() {
        List<Behavior> result = new ArrayList<Behavior>();
        for(Behavior behavior : this.dao.getAll()) {
            result.add(behavior);
        }
        return result;
    }
    
    public void updateBehavior(Behavior behavior) {
        this.dao.update(behavior);
    }
    
    public void deleteBehavior(int id) {
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
        return target instanceof Behavior;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Behavior behavior = (Behavior)target;
        return String.valueOf(behavior.id);
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
        
        Behavior behavior = (Behavior)target;
        return String.valueOf(behavior.id);
    }
}

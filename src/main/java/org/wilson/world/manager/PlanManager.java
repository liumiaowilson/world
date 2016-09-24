package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Plan;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PlanManager implements ItemTypeProvider {
    public static final String NAME = "plan";
    
    private static PlanManager instance;
    
    private DAO<Plan> dao = null;
    
    @SuppressWarnings("unchecked")
    private PlanManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Plan.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Plan plan : getPlans()) {
                    boolean found = plan.name.contains(text) || plan.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = plan.id;
                        content.name = plan.name;
                        content.description = plan.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PlanManager getInstance() {
        if(instance == null) {
            instance = new PlanManager();
        }
        return instance;
    }
    
    public void createPlan(Plan plan) {
        ItemManager.getInstance().checkDuplicate(plan);
        
        this.dao.create(plan);
    }
    
    public Plan getPlan(int id) {
        Plan plan = this.dao.get(id);
        if(plan != null) {
            return plan;
        }
        else {
            return null;
        }
    }
    
    public List<Plan> getPlans() {
        List<Plan> result = new ArrayList<Plan>();
        for(Plan plan : this.dao.getAll()) {
            result.add(plan);
        }
        return result;
    }
    
    public void updatePlan(Plan plan) {
        this.dao.update(plan);
    }
    
    public void deletePlan(int id) {
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
        return target instanceof Plan;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Plan plan = (Plan)target;
        return String.valueOf(plan.id);
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
        
        Plan plan = (Plan)target;
        return plan.name;
    }
}

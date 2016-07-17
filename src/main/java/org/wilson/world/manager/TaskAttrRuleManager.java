package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TaskAttrRule;

public class TaskAttrRuleManager implements ItemTypeProvider {
    public static final String NAME = "task_attr_rule";
    
    private static TaskAttrRuleManager instance;
    
    private DAO<TaskAttrRule> dao = null;
    
    @SuppressWarnings("unchecked")
    private TaskAttrRuleManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskAttrRule.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static TaskAttrRuleManager getInstance() {
        if(instance == null) {
            instance = new TaskAttrRuleManager();
        }
        return instance;
    }
    
    public void createTaskAttrRule(TaskAttrRule rule) {
        this.dao.create(rule);
    }
    
    public TaskAttrRule getTaskAttrRule(int id) {
        TaskAttrRule rule = this.dao.get(id);
        if(rule != null) {
            return rule;
        }
        else {
            return null;
        }
    }
    
    public List<TaskAttrRule> getTaskAttrRules() {
        List<TaskAttrRule> result = new ArrayList<TaskAttrRule>();
        for(TaskAttrRule rule : this.dao.getAll()) {
            result.add(rule);
        }
        return result;
    }
    
    public void updateTaskAttrRule(TaskAttrRule rule) {
        this.dao.update(rule);
    }
    
    public void deleteTaskAttrRule(int id) {
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
        return target instanceof TaskAttrRule;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskAttrRule rule = (TaskAttrRule)target;
        return String.valueOf(rule.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<String> getAvailableNames() {
        List<String> ret = new ArrayList<String>();
        List<String> all = TaskAttrDefManager.getInstance().getTaskAttrNames();
        List<String> used = new ArrayList<String>();
        for(TaskAttrRule rule : this.getTaskAttrRules()) {
            used.add(rule.name);
        }
        for(String name : all) {
            if(!used.contains(name) && !name.equals(TaskAttrDefManager.DEF_BEFORE) && !name.equals(TaskAttrDefManager.DEF_AFTER)) {
                ret.add(name);
            }
        }
        return ret;
    }
}

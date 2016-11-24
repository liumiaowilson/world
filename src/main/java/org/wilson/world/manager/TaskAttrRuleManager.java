package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.TaskAttrRule;
import org.wilson.world.task.DefaultTaskAttrComparator;
import org.wilson.world.task.DefaultTaskSortChainItem;
import org.wilson.world.task.TaskAttrComparator;
import org.wilson.world.task.TaskSortChainItem;

public class TaskAttrRuleManager implements ItemTypeProvider, ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(TaskAttrRuleManager.class);
    
    public static final String NAME = "task_attr_rule";
    
    private static TaskAttrRuleManager instance;
    
    private DAO<TaskAttrRule> dao = null;
    
    private Map<String, TaskAttrComparator> comparators = null;
    
    private TaskSortChainItem root = null;
    
    @SuppressWarnings("unchecked")
    private TaskAttrRuleManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskAttrRule.class);
        
        this.comparators = new HashMap<String, TaskAttrComparator>();
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static TaskAttrRuleManager getInstance() {
        if(instance == null) {
            instance = new TaskAttrRuleManager();
        }
        return instance;
    }
    
    public void createTaskAttrRule(TaskAttrRule rule) {
        ItemManager.getInstance().checkDuplicate(rule);
        
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
        List<TaskAttrRule> result = this.dao.getAll();
        Collections.sort(result, new Comparator<TaskAttrRule>(){
            @Override
            public int compare(TaskAttrRule o1, TaskAttrRule o2) {
                return -(o1.priority - o2.priority);
            }
        });
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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

    @Override
    public void start() {
        logger.info("Load task attr comparators...");
        for(TaskAttrRule rule : this.getTaskAttrRules()) {
            this.loadTaskAttrComparator(rule);
        }
        
        logger.info("Initialize task sort chain...");
        this.initTaskSortChain();
    }
    
    private void initTaskSortChain() {
        List<TaskAttrRule> rules = this.getTaskAttrRules();
        List<DefaultTaskSortChainItem> items = new ArrayList<DefaultTaskSortChainItem>(rules.size());
        for(TaskAttrRule rule : rules) {
            DefaultTaskSortChainItem item = new DefaultTaskSortChainItem(rule.name, null);
            items.add(item);
        }
        for(int i = 0; i < items.size() - 2; i++) {
            items.get(i).setNext(items.get(i + 1));
        }
        
        if(!items.isEmpty()) {
            this.root = items.get(0);
        }
    }
    
    private void loadTaskAttrComparator(TaskAttrRule rule) {
        TaskAttrComparator comparator = null;
        if(StringUtils.isBlank(rule.impl)) {
            boolean reversed = "reversed".equals(rule.policy);
            comparator = new DefaultTaskAttrComparator(reversed);
        }
        else {
        	comparator = (TaskAttrComparator) ExtManager.getInstance().getExtension(rule.impl, TaskAttrComparator.class);
        }
        if(comparator != null) {
            this.comparators.put(rule.name, comparator);
        }
    }

    @Override
    public void shutdown() {
    }
    
    public TaskAttrComparator getTaskAttrComparator(String name) {
        return this.comparators.get(name);
    }
    
    public TaskSortChainItem getTaskSortChainItem() {
        return this.root;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return null;
    }
}

package org.wilson.world.dao;

import java.util.Collections;
import java.util.List;

import org.wilson.world.model.TaskAttrRule;

public class TaskAttrRuleMemInit implements MemInit<TaskAttrRule> {
    @Override
    public void init(DAO<TaskAttrRule> dao) {
        dao.create(this.createTaskAttrRule("Priority", 4, "reversed"));
        dao.create(this.createTaskAttrRule("Urgency", 3, "reversed"));
        dao.create(this.createTaskAttrRule("DueAt", 2, "normal"));
        dao.create(this.createTaskAttrRule("Effort", 1, "normal"));
    }
    
    private TaskAttrRule createTaskAttrRule(String name, int priority, String policy) {
        TaskAttrRule rule = new TaskAttrRule();
        rule.name = name;
        rule.priority = priority;
        rule.policy = policy;
        return rule;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QueryTemplate<TaskAttrRule>> getQueryTemplates() {
        return Collections.EMPTY_LIST;
    }
}

package org.wilson.world.penalty;

import java.util.List;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.PenaltyManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Penalty;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.RoutineTaskTemplateComponent;
import org.wilson.world.task.SystemTaskGenerator;
import org.wilson.world.task.TaskTemplate;

public class PenaltyTaskGenerator extends SystemTaskGenerator {
    public static final String NAME = "Penalty";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task spawn() {
        int karma = CharManager.getInstance().getKarma();
        if(karma >= 0) {
            return null;
        }
        
        int max_severity = PenaltyManager.getInstance().getMaxSeverity();
        if(max_severity < 0) {
            return null;
        }
        
        int max_karma = CharManager.getInstance().getMaxKarma();
        int p = -karma * 100 / max_karma;
        
        for(Penalty penalty : PenaltyManager.getInstance().getPenalties()) {
            if(DiceManager.getInstance().dice(p * penalty.severity / max_severity)) {

                Task task = new Task();
                task.name = "P " + penalty.name;
                if(task.name.length() > 20) {
                    task.name = task.name.substring(0, 20);
                }
                task.content = penalty.content;
                task.createdTime = System.currentTimeMillis();
                task.modifiedTime = task.createdTime;
                
                TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE + "_" + RoutineTaskTemplateComponent.NAME);
                if(template != null) {
                    List<TaskAttr> attrs = template.getTemplateAttributes();
                    TaskAttr attr = TaskAttr.getTaskAttr(attrs, TaskAttrDefManager.DEF_URGENCY);
                    if(attr != null) {
                        attr.value = String.valueOf("90");
                    }
                    else {
                        attr = TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "90");
                        attrs.add(attr);
                    }
                    task.attrs.addAll(attrs);
                }
                
                return task;
            }
        }
        
        return null;
    }

}

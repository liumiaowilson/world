package org.wilson.world.context;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;

public class ContextPenaltyEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        if(EventType.FinishTask == event.type) {
            Context current = ContextManager.getInstance().getCurrentContext();
            if(current != null) {
                Task task = (Task) event.data.get("data");
                if(task != null) {
                    TaskAttr attr = TaskManager.getInstance().getTaskAttr(task, TaskAttrDefManager.DEF_CONTEXT);
                    if(attr != null && !StringUtils.isBlank(attr.value)) {
                        if(!attr.value.equals(String.valueOf(current.id))) {
                            int exp = ExpManager.getInstance().getExp();
                            exp = exp - 2;
                            ExpManager.getInstance().setExp(exp);
                            
                            NotifyManager.getInstance().notifyDanger("Lost 2 experience points because of finishing tasks in the wrong context!");
                        }
                    }
                }
            }
        }
    }

}

package org.wilson.world.task;

import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.manager.TaskSeedManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;

public class TaskSeedWorker implements Runnable {
    private static final Logger logger = Logger.getLogger(TaskSeedWorker.class);
    
    private TimeZone tz;
    
    public TaskSeedWorker(TimeZone tz) {
        this.tz = tz;
    }
    
    @Override
    public void run() {
        Date date = new Date();
        for(TaskGenerator tg : TaskSeedManager.getInstance().getTaskGenerators().values()) {
            if(tg.canStart(tz, date)) {
                String name = tg.getName();
                Task oldTask = TaskManager.getInstance().findTask(TaskAttrDefManager.DEF_SEED, name);
                if(oldTask == null) {
                    Task task = tg.generateTask();
                    if(task != null) {
                        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_SEED);
                        if(attr == null) {
                            attr = TaskAttr.create(TaskAttrDefManager.DEF_SEED, name);
                            task.attrs.add(attr);
                        }
                        else {
                            attr.value = name;
                        }
                        TaskManager.getInstance().createTask(task);
                        logger.info("Spawned a task [" + task.name + "] from seed [" + name + "].");
                    }
                }
                else {
                    logger.info("Skip spawning task from seed [" + name + "] as it has already spawned [" + oldTask.name + "].");
                }
            }
        }
    }

}

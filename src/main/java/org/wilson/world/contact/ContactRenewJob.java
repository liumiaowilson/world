package org.wilson.world.contact;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ContactManager;
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Contact;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.schedule.DefaultJob;
import org.wilson.world.task.OutdoorTaskTemplateComponent;
import org.wilson.world.task.TaskTemplate;

public class ContactRenewJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(ContactRenewJob.class);
    
    public static final long DAY_DURATION = 24 * 60 * 60 * 1000L;
    
    @Override
    public void execute() {
        logger.info("Start to renew contacts...");
        int cycle = ConfigManager.getInstance().getConfigAsInt("contact.renew.cycle", 30);
        List<Contact> contacts = ContactManager.getInstance().getContacts();
        long period = cycle * DAY_DURATION;
        long now = System.currentTimeMillis();
        List<Contact> renewList = new ArrayList<Contact>();
        for(Contact contact : contacts) {
            if(contact.modifiedTime + period < now) {
                renewList.add(contact);
            }
        }
        
        if(renewList.isEmpty()) {
            logger.info("Find no contacts to renew.");
        }
        else {
            for(Contact contact : renewList) {
                String seed = this.getJobName() + ":" + contact.name;
                Task oldTask = TaskManager.getInstance().findTask(TaskAttrDefManager.DEF_SEED, seed);
                if(oldTask != null) {
                    logger.info("Renew task already exists for [" + contact.name + "].");
                    //skip
                }
                else {
                    Task task = new Task();
                    String name = contact.name;
                    if(name.length() > 14) {
                        name = name.substring(0, 14);
                    }
                    
                    task.name = "Renew " + name;
                    task.content = "Please start to renew contact [" + contact.name + "].";
                    task.createdTime = System.currentTimeMillis();
                    task.modifiedTime = task.createdTime;
                    
                    TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE + "_" + OutdoorTaskTemplateComponent.NAME);
                    if(template != null) {
                        List<TaskAttr> attrs = template.getTemplateAttributes();
                        TaskAttr attr = TaskAttr.getTaskAttr(attrs, TaskAttrDefManager.DEF_PRIORITY);
                        if(attr != null) {
                            attr.value = String.valueOf("80");
                        }
                        else {
                            attr = TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "80");
                            attrs.add(attr);
                        }
                        task.attrs.addAll(attrs);
                    }
                    
                    Task old = TaskManager.getInstance().getTask(task.name);
                    if(old == null) {
                        TaskManager.getInstance().createTask(task);
                    }
                    else {
                        logger.warn("A task with the same name [" + task.name + "] already exists.");
                    }
                }
            }
        }
    }

}

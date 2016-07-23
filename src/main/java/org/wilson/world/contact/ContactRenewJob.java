package org.wilson.world.contact;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ContactManager;
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Contact;
import org.wilson.world.model.Context;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.schedule.DefaultJob;

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
                    task.name = contact.name;
                    task.content = "Please start to renew contact [" + contact.name + "].";
                    Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_LEISURE);
                    if(context != null) {
                        task.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
                    }
                    task.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "80"));
                    task.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_SEED, seed));
                    
                    TaskManager.getInstance().createTask(task);
                }
            }
        }
    }

}

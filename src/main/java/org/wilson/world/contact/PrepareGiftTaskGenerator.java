package org.wilson.world.contact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ContactAttrDefManager;
import org.wilson.world.manager.ContactManager;
import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Contact;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.RelationshipTaskTemplateComponent;
import org.wilson.world.task.SystemTaskGenerator;
import org.wilson.world.task.TaskTemplate;
import org.wilson.world.util.TimeUtils;

public class PrepareGiftTaskGenerator extends SystemTaskGenerator {
    public static final String NAME = "PrepareGift";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task spawn() {
        return null;
    }

    @Override
    protected List<Task> spawnTasks() {
        List<Task> ret = new ArrayList<Task>();
        
        int days = ConfigManager.getInstance().getConfigAsInt("birthday.gift.task.ahead.days", 30);
        TimeZone tz = this.getTimeZone();
        List<Contact> contacts = ContactManager.getInstance().getContacts();
        for(Contact contact : contacts) {
            String birthday = contact.getValue(ContactAttrDefManager.DEF_BIRTHDAY);
            if(!StringUtils.isBlank(birthday)) {
                Date birthdayDate = TimeUtils.parseDate(birthday, tz);
                if(birthdayDate != null) {
                    Date nextBirthday = getNextBirthday(birthdayDate);
                    long remaining_time = nextBirthday.getTime() - System.currentTimeMillis();
                    if(remaining_time < days * TimeUtils.DAY_DURATION) {
                        Task task = this.spawn(contact);
                        if(task != null) {
                            ret.add(task);
                        }
                    }
                }
            }
        }
        
        return ret;
    }
    
    public Task spawn(Contact contact) {
        Task task = new Task();
        String name = contact.name;
        if(name.length() > 15) {
            name = name.substring(0, 15);
        }
        task.name = "Gift " + name;
        task.content = "Prepare gift for [" + contact.name + "]";
        task.createdTime = System.currentTimeMillis();
        task.modifiedTime = task.createdTime;
        
        TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE + "_" + RelationshipTaskTemplateComponent.NAME);
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
        
        return task;
    }

    public static Date getNextBirthday(Date birthdayDate) {
        Date now = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int year = cal.get(Calendar.YEAR);
        
        cal.setTime(birthdayDate);
        cal.set(Calendar.YEAR, year);
        
        if(cal.getTimeInMillis() > now.getTime()) {
            return cal.getTime();
        }
        
        cal.set(Calendar.YEAR, year + 1);
        return cal.getTime();
    }
}

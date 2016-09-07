package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Reminder;
import org.wilson.world.reminder.ReminderMonitor;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.TimeUtils;

public class ReminderManager implements ItemTypeProvider {
    public static final String NAME = "reminder";
    
    private static ReminderManager instance;
    
    private DAO<Reminder> dao = null;
    
    @SuppressWarnings("unchecked")
    private ReminderManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Reminder.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Reminder reminder : getReminders()) {
                    boolean found = reminder.name.contains(text) || reminder.message.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = reminder.id;
                        content.name = reminder.name;
                        content.description = reminder.message;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        MonitorManager.getInstance().registerMonitorParticipant(new ReminderMonitor());
    }
    
    public static ReminderManager getInstance() {
        if(instance == null) {
            instance = new ReminderManager();
        }
        return instance;
    }
    
    public void createReminder(Reminder reminder) {
        ItemManager.getInstance().checkDuplicate(reminder);
        
        this.dao.create(reminder);
    }
    
    public Reminder getReminder(int id) {
        Reminder reminder = this.dao.get(id);
        if(reminder != null) {
            return reminder;
        }
        else {
            return null;
        }
    }
    
    public List<Reminder> getReminders() {
        List<Reminder> result = new ArrayList<Reminder>();
        for(Reminder reminder : this.dao.getAll()) {
            result.add(reminder);
        }
        return result;
    }
    
    public void updateReminder(Reminder reminder) {
        this.dao.update(reminder);
    }
    
    public void deleteReminder(int id) {
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
        return target instanceof Reminder;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Reminder reminder = (Reminder)target;
        return String.valueOf(reminder.id);
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
        
        Reminder reminder = (Reminder)target;
        return reminder.name;
    }
    
    public List<Reminder> getExpiredReminders() {
        List<Reminder> ret = new ArrayList<Reminder>();
        
        long now = System.currentTimeMillis();
        for(Reminder reminder : this.getReminders()) {
            if(reminder.time + reminder.hours * TimeUtils.HOUR_DURATION + reminder.minutes + TimeUtils.MINUTE_DURATION < now) {
                ret.add(reminder);
            }
        }
        
        return ret;
    }
    
    public String getRemainingTimeDisplay(Reminder reminder) {
        if(reminder == null) {
            return "";
        }
        
        long expected = reminder.time + reminder.hours * TimeUtils.HOUR_DURATION + reminder.minutes * TimeUtils.MINUTE_DURATION;
        return TimeUtils.getRemainingTime(expected);
    }
}

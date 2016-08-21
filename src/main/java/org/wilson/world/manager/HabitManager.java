package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.dao.DAO;
import org.wilson.world.habit.HabitIdeaConverter;
import org.wilson.world.habit.HabitSystemBehaviorDefProvider;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Habit;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class HabitManager implements ItemTypeProvider {
    public static final String NAME = "habit";
    
    private static HabitManager instance;
    
    private DAO<Habit> dao = null;
    
    @SuppressWarnings("unchecked")
    private HabitManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Habit.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new HabitIdeaConverter());
        
        BehaviorDefManager.getInstance().addSystemBehaviorDefProvider(new HabitSystemBehaviorDefProvider());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Habit habit : getHabits()) {
                    boolean found = habit.name.contains(text) || habit.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = habit.id;
                        content.name = habit.name;
                        content.description = habit.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static HabitManager getInstance() {
        if(instance == null) {
            instance = new HabitManager();
        }
        return instance;
    }
    
    public void createHabit(Habit habit) {
        ItemManager.getInstance().checkDuplicate(habit);
        
        this.dao.create(habit);
    }
    
    public Habit getHabit(int id) {
        Habit habit = this.dao.get(id);
        if(habit != null) {
            return habit;
        }
        else {
            return null;
        }
    }
    
    public List<Habit> getHabits() {
        List<Habit> result = new ArrayList<Habit>();
        for(Habit habit : this.dao.getAll()) {
            result.add(habit);
        }
        return result;
    }
    
    public void updateHabit(Habit habit) {
        this.dao.update(habit);
    }
    
    public void deleteHabit(int id) {
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
        return target instanceof Habit;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Habit habit = (Habit)target;
        return String.valueOf(habit.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<Habit> getCheckableHabits(TimeZone tz) {
        List<Habit> ret = new ArrayList<Habit>();
        for(Habit habit : this.dao.getAll()) {
            if(HabitTraceManager.getInstance().isHabitCheckable(habit, tz)) {
                ret.add(habit);
            }
        }
        return ret;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Habit habit = (Habit)target;
        return habit.name;
    }
}

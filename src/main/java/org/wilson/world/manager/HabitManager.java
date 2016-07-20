package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Habit;

public class HabitManager implements ItemTypeProvider {
    public static final String NAME = "habit";
    
    private static HabitManager instance;
    
    private DAO<Habit> dao = null;
    
    @SuppressWarnings("unchecked")
    private HabitManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Habit.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static HabitManager getInstance() {
        if(instance == null) {
            instance = new HabitManager();
        }
        return instance;
    }
    
    public void createHabit(Habit habit) {
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

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

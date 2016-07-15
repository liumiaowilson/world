package org.wilson.world.manager;

import org.wilson.world.character.DisasterJob;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class CharManager implements EventListener{
    private static CharManager instance;
    
    private CharManager() {
        ScheduleManager.getInstance().addJob(new DisasterJob());
        
        EventManager.getInstance().registerListener(EventType.GainExperience, this);
    }
    
    public static CharManager getInstance() {
        if(instance == null) {
            instance = new CharManager();
        }
        return instance;
    }
    
    public int getMaxHP() {
        return DataManager.getInstance().getValueAsInt("user.max_hp");
    }
    
    public void setMaxHP(int max_hp) {
        DataManager.getInstance().setValue("user.max_hp", max_hp);
    }
    
    public int getHP() {
        return DataManager.getInstance().getValueAsInt("user.hp");
    }
    
    public void setHP(int hp) {
        DataManager.getInstance().setValue("user.hp", hp);
    }
    
    public int getCurrentHPPercentage() {
        int max_hp = this.getMaxHP();
        int hp = this.getHP();
        int pct = (int) (hp * 100.0 / max_hp);
        return pct;
    }
    
    public void restore() {
        int hp = this.getMaxHP();
        this.setHP(hp);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        boolean isProtected = DataManager.getInstance().getValueAsBoolean("user.protected");
        if(isProtected) {
            DataManager.getInstance().setValue("user.protected", false);
        }
    }
}

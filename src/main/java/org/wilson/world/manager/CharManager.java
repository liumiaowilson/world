package org.wilson.world.manager;

public class CharManager {
    private static CharManager instance;
    
    private CharManager() {
        
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
}

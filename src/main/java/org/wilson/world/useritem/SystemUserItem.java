package org.wilson.world.useritem;

public abstract class SystemUserItem implements UserItem {
    private int id;
    private UserItemEffect effect;
    
    public SystemUserItem(UserItemEffect effect) {
        this.effect = effect;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getDescription() {
        return this.getName();
    }

    @Override
    public void takeEffect() {
        if(this.effect != null) {
            this.effect.takeEffect();
        }
    }
}

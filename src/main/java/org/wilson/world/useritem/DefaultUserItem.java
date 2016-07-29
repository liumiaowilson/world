package org.wilson.world.useritem;

import org.wilson.world.model.UserItemData;

public class DefaultUserItem implements UserItem {
    private UserItemData data;
    private UserItemEffect effect;
    
    public DefaultUserItem(UserItemData data, UserItemEffect effect) {
        this.data = data;
        this.effect = effect;
    }
    
    @Override
    public void setId(int id) {
    }

    @Override
    public int getId() {
        return data.id;
    }

    @Override
    public String getName() {
        return data.name;
    }

    @Override
    public String getType() {
        return data.type;
    }

    @Override
    public String getDescription() {
        return data.description;
    }

    @Override
    public void takeEffect() {
        effect.takeEffect();
    }

    @Override
    public int getValue() {
        return data.value;
    }

}

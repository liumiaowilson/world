package org.wilson.world.useritem;

import java.util.ArrayList;
import java.util.List;

public class UserItemFactory {
    private List<UserItem> items = new ArrayList<UserItem>();
    
    private static UserItemFactory instance;
    
    private UserItemFactory() {
        this.addUserItem("HP Minor Potion", UserItemType.Potion.name(), "Recove minor amount of HP", 5, new HPRecoverEffect(25));
        this.addUserItem("HP Medium Potion", UserItemType.Potion.name(), "Recove medium amount of HP", 10, new HPRecoverEffect(50));
        this.addUserItem("HP Major Potion", UserItemType.Potion.name(), "Recove major amount of HP", 15, new HPRecoverEffect(70));
    }
    
    public void addUserItem(String name, String type, String description, int value, UserItemEffect effect) {
        UserItem item = this.createUserItem(name, type, description, value, effect);
        this.items.add(item);
    }
    
    public List<UserItem> getUserItems() {
        return this.items;
    }
    
    public UserItem createUserItem(String name, String type, String description, int value, UserItemEffect effect) {
        CommonUserItem item = new CommonUserItem(effect);
        item.setName(name);
        item.setType(type);
        item.setDescription(description);
        item.setValue(value);
        return item;
    }
    
    public static UserItemFactory getInstance() {
        if(instance == null) {
            instance = new UserItemFactory();
        }
        return instance;
    }
}

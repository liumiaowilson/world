package org.wilson.world.useritem;

public class HPPotion extends SystemUserItem {
    public static final String NAME = "HP Potion";
    
    public HPPotion(UserItemEffect effect) {
        super(effect);
    }
    
    public HPPotion() {
        this(new HPRecoverEffect());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Recover a medium amount of HP at a time";
    }

    @Override
    public String getType() {
        return UserItemType.Potion.name();
    }

    @Override
    public int getValue() {
        return 50;
    }

}

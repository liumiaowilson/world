package org.wilson.world.useritem;

public class MinorHPPotion extends SystemUserItem {
    public static final String NAME = "Minor HP Potion";
    
    public MinorHPPotion(UserItemEffect effect) {
        super(effect);
    }
    
    public MinorHPPotion() {
        this(new MinorHPRecoverEffect());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Recover a minor amount of HP at a time";
    }

    @Override
    public String getType() {
        return UserItemType.Potion.name();
    }

    @Override
    public int getValue() {
        return 25;
    }

}

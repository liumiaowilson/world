package org.wilson.world.useritem;

public class MajorHPPotion extends SystemUserItem {
    public static final String NAME = "Major HP Potion";
    
    public MajorHPPotion(UserItemEffect effect) {
        super(effect);
    }
    
    public MajorHPPotion() {
        this(new MajorHPRecoverEffect());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Recover a major amount of HP at a time";
    }

    @Override
    public String getType() {
        return UserItemType.Potion.name();
    }

    @Override
    public int getValue() {
        return 75;
    }

}

package org.wilson.world.skill;

public abstract class SystemSkill implements Skill {
    private int id;
    
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}

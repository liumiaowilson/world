package org.wilson.world.behavior;

public class SystemBehaviorDef implements IBehaviorDef {
    private int id;
    private String name;
    private String description;
    private int karma;
    
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getKarma() {
        return karma;
    }

}

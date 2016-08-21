package org.wilson.world.behavior;

import org.wilson.world.model.BehaviorDef;

public class DefaultBehaviorDef implements IBehaviorDef {
    private BehaviorDef def;
    
    public DefaultBehaviorDef(BehaviorDef def) {
        this.def = def;
    }
    
    @Override
    public int getId() {
        return this.def.id;
    }

    @Override
    public void setId(int id) {
    }

    @Override
    public String getName() {
        return this.def.name;
    }

    @Override
    public String getDescription() {
        return this.def.description;
    }

    @Override
    public int getKarma() {
        return this.def.karma;
    }

}

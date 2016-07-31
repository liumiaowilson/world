package org.wilson.world.tick;

import org.wilson.world.manager.TickManager;

public abstract class Actor implements Tickable {
    private int turnId;
    private String name;
    private int speed;
    
    @Override
    public int getTurnId() {
        return this.turnId;
    }

    @Override
    public void setTurnId(int turnId) {
        this.turnId = turnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public int tick(int stepId, TickMonitor monitor) {
        this.doTick(stepId, monitor);
        
        int speed = this.getSpeed();
        if(speed < 10) {
            speed = 10;
        }
        else if(speed > 100) {
            speed = 100;
        }
        int ret = 100 / speed;
        return ret;
    }
    
    public String toString() {
        return this.getName();
    }
    
    public Actor getTarget() {
        Tickable tickable = TickManager.getInstance().getTarget(this);
        if(tickable instanceof Actor) {
            return (Actor)tickable;
        }
        else {
            return null;
        }
    }
    
    public TickMessage message(String message) {
        return message(null, message);
    }
    
    public TickMessage message(Actor target, String message) {
        TickMessage m = new TickMessage();
        m.source = this;
        m.target = target;
        m.message = message;
        return m;
    }

    public abstract void doTick(int stepId, TickMonitor monitor);
}

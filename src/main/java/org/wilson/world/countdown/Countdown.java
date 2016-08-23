package org.wilson.world.countdown;

import java.util.Date;

public class Countdown implements ICountdown {
    private String name;
    private Date target;
    
    public Countdown(String name, Date target) {
        this.name = name;
        this.target = target;
    }
    
    public void setTarget(Date target) {
        this.target = target;
    }
    
    @Override
    public Date getTarget() {
        return this.target;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

}

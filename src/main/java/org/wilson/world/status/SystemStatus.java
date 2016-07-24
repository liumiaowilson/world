package org.wilson.world.status;

public abstract class SystemStatus implements IStatus {
    private int id;
    
    public void setID(int id) {
        this.id = id;
    }
    
    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getDescription() {
        return this.getName();
    }

    @Override
    public String getIcon() {
        return this.getName() + ".png";
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

}

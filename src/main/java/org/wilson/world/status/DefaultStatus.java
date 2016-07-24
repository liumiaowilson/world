package org.wilson.world.status;

import org.wilson.world.model.Status;

public class DefaultStatus implements IStatus {
    private Status status;
    private StatusActivator activator;
    private StatusDeactivator deactivator;
    
    public DefaultStatus(Status status, StatusActivator activator, StatusDeactivator deactivator) {
        this.status = status;
        this.activator = activator;
        this.deactivator = deactivator;
    }
    
    @Override
    public int getID() {
        return this.status.id;
    }

    @Override
    public String getName() {
        return this.status.name;
    }

    @Override
    public String getDescription() {
        return this.status.description;
    }

    @Override
    public String getIcon() {
        return this.status.icon;
    }

    @Override
    public void activate() {
        this.activator.activate();
    }

    @Override
    public void deactivate() {
        this.deactivator.deactivate();
    }

}

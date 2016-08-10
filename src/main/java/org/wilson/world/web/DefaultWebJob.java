package org.wilson.world.web;

import org.wilson.world.model.Hopper;

public class DefaultWebJob implements WebJob {
    private Hopper hopper;
    private WebJobExecutor executor;
    
    public DefaultWebJob(Hopper hopper, WebJobExecutor executor) {
        this.hopper = hopper;
        this.executor = executor;
    }
    
    @Override
    public void setId(int id) {
    }

    @Override
    public int getId() {
        return this.hopper.id;
    }

    @Override
    public String getName() {
        return this.hopper.name;
    }

    @Override
    public String getDescription() {
        return this.hopper.description;
    }

    @Override
    public int getPeriod() {
        return this.hopper.period;
    }

    @Override
    public void run(WebJobMonitor monitor) throws Exception {
        this.executor.execute(monitor);
    }

}

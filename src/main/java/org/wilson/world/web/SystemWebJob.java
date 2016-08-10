package org.wilson.world.web;

public abstract class SystemWebJob implements WebJob {
    private int id;
    private String name;
    private String description;
    private int period;
    private WebJobMonitor monitor;
    
    public SystemWebJob() {
        String name = this.getClass().getSimpleName();
        if(name.length() > 20) {
            name = name.substring(0, 20);
        }
        this.setName(name);
        this.setDescription(name);
        this.setPeriod(1);
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getPeriod() {
        return this.period;
    }
    
    public void run(WebJobMonitor monitor) throws Exception{
        this.run(monitor, 1);
    }
    
    public void run(WebJobMonitor monitor, int total) throws Exception{
        this.monitor = monitor;
        
        monitor.start(total);
        
        try {
            this.run();
            
            monitor.succeed();
        }
        catch(Exception e) {
            monitor.fail();
            throw e;
        }
    }
    
    protected WebJobMonitor getMonitor() {
        return this.monitor;
    }
    
    public abstract void run() throws Exception;
}

package org.wilson.world.joke;

import org.wilson.world.manager.JokeManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractJokeJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        JokeInfo selected = JokeManager.getInstance().getSelected();
        if(selected == null) {
            JokeManager.getInstance().clear(this.getFrom());
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(JokeInfo info) throws Exception;
}

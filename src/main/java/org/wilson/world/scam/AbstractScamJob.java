package org.wilson.world.scam;

import org.wilson.world.manager.ScamManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractScamJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        ScamInfo selected = ScamManager.getInstance().getSelected();
        if(selected == null) {
            ScamManager.getInstance().clear(this.getFrom());
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(ScamInfo info) throws Exception;
}

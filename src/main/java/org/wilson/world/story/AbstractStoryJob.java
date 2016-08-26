package org.wilson.world.story;

import org.wilson.world.manager.StoryManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractStoryJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        StoryInfo selected = StoryManager.getInstance().getSelected();
        if(selected == null) {
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(StoryInfo info) throws Exception;
}

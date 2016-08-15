package org.wilson.world.novel;

import org.wilson.world.manager.NovelManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractNovelJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        NovelInfo selected = NovelManager.getInstance().getSelected();
        if(selected == null) {
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(NovelInfo info) throws Exception;
}

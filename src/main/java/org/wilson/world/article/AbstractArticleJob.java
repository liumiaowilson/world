package org.wilson.world.article;

import org.wilson.world.manager.ArticleManager;
import org.wilson.world.web.SystemWebJob;

public abstract class AbstractArticleJob extends SystemWebJob {
    protected String getFrom() {
        String name = this.getName();
        if(name.endsWith("Job")) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
    
    @Override
    public void run() throws Exception {
        ArticleInfo selected = ArticleManager.getInstance().getSelected();
        if(selected == null) {
            ArticleManager.getInstance().clear(this.getFrom());
            this.loadList();
        }
        else {
            this.loadSingle(selected);
        }
    }
    
    public abstract void loadList() throws Exception;
    
    public abstract void loadSingle(ArticleInfo info) throws Exception;
}

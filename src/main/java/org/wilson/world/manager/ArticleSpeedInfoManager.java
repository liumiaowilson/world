package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.ArticleSpeedInfo;

public class ArticleSpeedInfoManager {
    private static ArticleSpeedInfoManager instance;
    
    private DAO<ArticleSpeedInfo> dao = null;
    
    @SuppressWarnings("unchecked")
    private ArticleSpeedInfoManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ArticleSpeedInfo.class);
    }
    
    public static ArticleSpeedInfoManager getInstance() {
        if(instance == null) {
            instance = new ArticleSpeedInfoManager();
        }
        return instance;
    }
    
    public void createArticleSpeedInfo(ArticleSpeedInfo info) {
        this.dao.create(info);
    }
    
    public ArticleSpeedInfo getArticleSpeedInfo(int id) {
        ArticleSpeedInfo info = this.dao.get(id);
        if(info != null) {
            return info;
        }
        else {
            return null;
        }
    }
    
    public List<ArticleSpeedInfo> getArticleSpeedInfos() {
        List<ArticleSpeedInfo> result = new ArrayList<ArticleSpeedInfo>();
        for(ArticleSpeedInfo info : this.dao.getAll()) {
            result.add(info);
        }
        return result;
    }
    
    public void updateArticleSpeedInfo(ArticleSpeedInfo info) {
        this.dao.update(info);
    }
    
    public void deleteArticleSpeedInfo(int id) {
        this.dao.delete(id);
    }
}

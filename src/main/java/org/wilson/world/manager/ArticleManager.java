package org.wilson.world.manager;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.article.ArticleSpeedTrainResult;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.ArticleInfo;
import org.wilson.world.web.ArticleListJob;
import org.wilson.world.web.ArticleLoadJob;
import org.wilson.world.web.WebJob;

public class ArticleManager {
    private static ArticleManager instance;
    
    private ArticleManager() {
        
    }
    
    public static ArticleManager getInstance() {
        if(instance == null) {
            instance = new ArticleManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public ArticleInfo randomArticleInfo() {
        List<ArticleInfo> infos = (List<ArticleInfo>) WebManager.getInstance().get(ArticleListJob.ARTICLE_LIST);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        ArticleInfo info = infos.get(n);
        
        if(!info.loaded) {
            //clean loaded infos
            for(ArticleInfo i : infos) {
                if(i.loaded) {
                    i.html = null;
                    i.text = null;
                    i.loaded = false;
                }
            }
            
            WebJob job = WebManager.getInstance().getAvailableWebJobByName(ArticleLoadJob.class.getSimpleName());
            if(job != null) {
                WebManager.getInstance().put(ArticleLoadJob.ARTICLE_INFO, info);
                
                WebManager.getInstance().run(job);
            }
        }
        
        return info;
    }
    
    @SuppressWarnings("unchecked")
    public ArticleInfo getArticleInfo(String title) {
        if(StringUtils.isBlank(title)) {
            return null;
        }
        
        List<ArticleInfo> infos = (List<ArticleInfo>) WebManager.getInstance().get(ArticleListJob.ARTICLE_LIST);
        if(infos == null) {
            return null;
        }
        
        for(ArticleInfo info : infos) {
            if(title.equals(info.title)) {
                return info;
            }
        }
        
        return null;
    }
    
    public ArticleSpeedTrainResult trainSpeed(String title, long startTime, long endTime) {
        ArticleSpeedTrainResult result = new ArticleSpeedTrainResult();
        if(StringUtils.isBlank(title)) {
            result.errorMessage = "Invalid article title";
            return result;
        }
        
        ArticleInfo info = this.getArticleInfo(title);
        if(info == null) {
            result.errorMessage = "No such article info found";
            return result;
        }
        
        if(!info.loaded) {
            result.errorMessage = "Article is not loaded";
            return result;
        }
        
        int words = this.getNumOfWords(info.text);
        double speed = words * 1.0 / (endTime - startTime);
        result.wordsPerMinute = (int) (speed * TimeUtils.MINUTE_DURATION);
        
        return result;
    }
    
    private int getNumOfWords(String content) {
        int sum = 0;
        for(String item : content.split(" ")) {
            if(!StringUtils.isBlank(item)) {
                sum++;
            }
        }
        return sum;
    }
}

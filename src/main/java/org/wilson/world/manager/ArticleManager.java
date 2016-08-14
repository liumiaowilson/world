package org.wilson.world.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.article.ArticleSpeedTrainResult;
import org.wilson.world.model.ArticleSpeedInfo;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.ArticleInfo;
import org.wilson.world.web.ArticleListJob;
import org.wilson.world.web.ArticleLoadJob;
import org.wilson.world.web.WebJob;

public class ArticleManager {
    private static ArticleManager instance;
    
    private ArticleInfo trainArticle = new ArticleInfo();
    
    private ArticleManager() {
        
    }
    
    public static ArticleManager getInstance() {
        if(instance == null) {
            instance = new ArticleManager();
        }
        return instance;
    }
    
    public void setTrainArticleInfo(ArticleInfo info) {
        this.trainArticle.title = info.title;
        this.trainArticle.url = info.url;
        this.trainArticle.html = info.html;
        this.trainArticle.text = info.text;
        this.trainArticle.loaded = info.loaded;
        this.trainArticle.expectedTime = info.expectedTime;
    }
    
    public void resetTrainArticleInfo() {
        this.trainArticle.title = null;
        this.trainArticle.url = null;
        this.trainArticle.html = null;
        this.trainArticle.text = null;
        this.trainArticle.loaded = false;
        this.trainArticle.expectedTime = 0;
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
        
        int [] speed = this.getArrayOfWPM();
        int avg_speed = speed[0];
        if(avg_speed != 0) {
            info.expectedTime = this.getNumOfWords(info.text) / avg_speed;
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
        
        ArticleInfo info = this.trainArticle;
        if(info == null) {
            result.errorMessage = "No such article info found";
            return result;
        }
        
        if(!info.loaded) {
            result.errorMessage = "Article is not loaded";
            return result;
        }
        
        int words = this.getNumOfWords(info.text);
        long time = endTime - startTime;
        
        ArticleSpeedInfo speedInfo = new ArticleSpeedInfo();
        speedInfo.words = words;
        speedInfo.time = time;
        ArticleSpeedInfoManager.getInstance().createArticleSpeedInfo(speedInfo);
        
        result.wordsPerMinute = this.getWordsPerMinute(words, time);
        
        this.resetTrainArticleInfo();
        
        return result;
    }
    
    public int [] getArrayOfWPM() {
        int [] ret = new int [3];
        
        int total_words = 0;
        long total_time = 0;
        int max = 0;
        int min = 0;
        
        for(ArticleSpeedInfo info : ArticleSpeedInfoManager.getInstance().getArticleSpeedInfos()) {
            total_words += info.words;
            total_time += info.time;
            
            int wpm = this.getWordsPerMinute(info.words, info.time);
            if(wpm > max) {
                max = wpm;
            }
            if(min == 0 || wpm < min) {
                min = wpm;
            }
        }
        
        if(total_time == 0) {
            return null;
        }
        
        int avg = this.getWordsPerMinute(total_words, total_time);
        ret[0] = avg;
        ret[1] = min;
        ret[2] = max;
        
        return ret;
    }
    
    public Map<Integer, Integer> getSpeedStats() {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        
        for(ArticleSpeedInfo info : ArticleSpeedInfoManager.getInstance().getArticleSpeedInfos()) {
            int minutes = (int) (info.time / TimeUtils.MINUTE_DURATION);
            ret.put(info.words, minutes);
        }
        
        return ret;
    }
    
    private int getWordsPerMinute(int words, long time) {
        double speed = words * 1.0 / time;
        return (int) (speed * TimeUtils.MINUTE_DURATION);
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

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Emotion;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class EmotionManager implements ItemTypeProvider {
    public static final String NAME = "emotion";
    
    private static EmotionManager instance;
    
    private DAO<Emotion> dao = null;
    
    private Map<String, Emotion> emotions = new HashMap<String, Emotion>();
    
    @SuppressWarnings("unchecked")
    private EmotionManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Emotion.class);
        ((CachedDAO<Emotion>)this.dao).getCache().addCacheListener(new CacheListener<Emotion>(){

            @Override
            public void cachePut(Emotion old, Emotion v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                EmotionManager.this.emotions.put(v.name, v);
            }

            @Override
            public void cacheDeleted(Emotion v) {
                EmotionManager.this.emotions.remove(v.name);
            }

            @Override
            public void cacheLoaded(List<Emotion> all) {
            }

            @Override
            public void cacheLoading(List<Emotion> old) {
                EmotionManager.this.emotions.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Emotion emotion : getEmotions()) {
                    boolean found = emotion.name.contains(text) || emotion.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = emotion.id;
                        content.name = emotion.name;
                        content.description = emotion.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static EmotionManager getInstance() {
        if(instance == null) {
            instance = new EmotionManager();
        }
        return instance;
    }
    
    public void createEmotion(Emotion emotion) {
        ItemManager.getInstance().checkDuplicate(emotion);
        
        this.dao.create(emotion);
    }
    
    public Emotion getEmotion(int id) {
        Emotion emotion = this.dao.get(id);
        if(emotion != null) {
            return emotion;
        }
        else {
            return null;
        }
    }
    
    public List<Emotion> getEmotions() {
        List<Emotion> result = new ArrayList<Emotion>();
        for(Emotion emotion : this.dao.getAll()) {
            result.add(emotion);
        }
        return result;
    }
    
    public void updateEmotion(Emotion emotion) {
        this.dao.update(emotion);
    }
    
    public void deleteEmotion(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Emotion;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Emotion emotion = (Emotion)target;
        return String.valueOf(emotion.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Emotion emotion = (Emotion)target;
        return emotion.name;
    }
    
    public Emotion getEmotion(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        return this.emotions.get(name);
    }
    
    public List<QuizPair> getEmotionQuizPairs() {
        List<QuizPair> ret = new ArrayList<QuizPair>();
        
        int id = 1;
        for(String name : this.emotions.keySet()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = name;
            pair.bottom = name;
            ret.add(pair);
        }
        
        return ret;
    }
    
    public Emotion randomEmotion() {
        List<Emotion> emotions = this.getEmotions();
        if(emotions.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(emotions.size());
        return emotions.get(n);
    }
}

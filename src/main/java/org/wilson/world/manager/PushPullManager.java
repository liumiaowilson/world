package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.PushPull;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PushPullManager {
    public static final String NAME = "push_pull";
    
    private static PushPullManager instance;
    
    private DAO<PushPull> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private PushPullManager() {
        this.dao = DAOManager.getInstance().getDAO(PushPull.class);
        
        int id = 1;
        for(PushPull pushpull : this.getPushPulls()) {
            for(String example : pushpull.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = pushpull.name;
                this.pairs.put(pair.id, pair);
            }
        }
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(PushPull pushpull : getPushPulls()) {
                    boolean found = pushpull.name.contains(text) || pushpull.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = pushpull.id;
                        content.name = pushpull.name;
                        content.description = pushpull.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PushPullManager getInstance() {
        if(instance == null) {
            instance = new PushPullManager();
        }
        return instance;
    }
    
    public void createPushPull(PushPull pushpull) {
    }
    
    public PushPull getPushPull(int id) {
        PushPull pushpull = this.dao.get(id);
        if(pushpull != null) {
            return pushpull;
        }
        else {
            return null;
        }
    }
    
    public List<PushPull> getPushPulls() {
        List<PushPull> result = new ArrayList<PushPull>();
        for(PushPull pushpull : this.dao.getAll()) {
            result.add(pushpull);
        }
        return result;
    }
    
    public void updatePushPull(PushPull pushpull) {
    }
    
    public void deletePushPull(int id) {
    }
    
    public List<QuizPair> getPushPullQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
    
    public PushPull randomPushPull() {
        List<PushPull> pushpulls = this.dao.getAll();
        if(pushpulls.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(pushpulls.size());
        return pushpulls.get(n);
    }
}

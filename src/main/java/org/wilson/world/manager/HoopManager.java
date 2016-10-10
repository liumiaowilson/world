package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.Hoop;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class HoopManager {
    public static final String NAME = "hoop";
    
    private static HoopManager instance;
    
    private DAO<Hoop> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private HoopManager() {
        this.dao = DAOManager.getInstance().getDAO(Hoop.class);
        
        int id = 1;
        for(Hoop hoop : this.getHoops()) {
            for(String example : hoop.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = hoop.name;
                pair.url = "javascript:jumpTo('hoop_edit.jsp?id=" + hoop.id + "')";
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
                
                for(Hoop hoop : getHoops()) {
                    boolean found = hoop.name.contains(text) || hoop.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = hoop.id;
                        content.name = hoop.name;
                        content.description = hoop.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static HoopManager getInstance() {
        if(instance == null) {
            instance = new HoopManager();
        }
        return instance;
    }
    
    public void createHoop(Hoop hoop) {
    }
    
    public Hoop getHoop(int id) {
        Hoop hoop = this.dao.get(id);
        if(hoop != null) {
            return hoop;
        }
        else {
            return null;
        }
    }
    
    public List<Hoop> getHoops() {
        List<Hoop> result = new ArrayList<Hoop>();
        for(Hoop hoop : this.dao.getAll()) {
            result.add(hoop);
        }
        return result;
    }
    
    public void updateHoop(Hoop hoop) {
    }
    
    public void deleteHoop(int id) {
    }
    
    public List<QuizPair> getHoopQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
    
    public Hoop randomHoop() {
        List<Hoop> hoops = this.dao.getAll();
        if(hoops.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(hoops.size());
        return hoops.get(n);
    }
}

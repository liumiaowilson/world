package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.SOMP;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class SOMPManager {
    public static final String NAME = "somp";
    
    private static SOMPManager instance;
    
    private DAO<SOMP> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private SOMPManager() {
        this.dao = DAOManager.getInstance().getDAO(SOMP.class);
        
        int id = 1;
        for(SOMP pattern : this.getSOMPs()) {
            for(Entry<String, String> entry : pattern.examples.entrySet()) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "<p><b>" + entry.getKey() + "</b></p><p>" + entry.getValue() + "</p>";
                pair.bottom = pattern.name;
                pair.url = "javascript:jumpTo('somp_edit.jsp?id=" + pattern.id + "')";
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
                
                for(SOMP pattern : getSOMPs()) {
                    boolean found = pattern.name.contains(text) || pattern.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = pattern.id;
                        content.name = pattern.name;
                        content.description = pattern.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static SOMPManager getInstance() {
        if(instance == null) {
            instance = new SOMPManager();
        }
        return instance;
    }
    
    public void createSOMP(SOMP pattern) {
    }
    
    public SOMP getSOMP(int id) {
        SOMP pattern = this.dao.get(id);
        if(pattern != null) {
            return pattern;
        }
        else {
            return null;
        }
    }
    
    public List<SOMP> getSOMPs() {
        List<SOMP> result = new ArrayList<SOMP>();
        for(SOMP pattern : this.dao.getAll()) {
            result.add(pattern);
        }
        return result;
    }
    
    public void updateSOMP(SOMP pattern) {
    }
    
    public void deleteSOMP(int id) {
    }
    
    public List<QuizPair> getSOMPQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

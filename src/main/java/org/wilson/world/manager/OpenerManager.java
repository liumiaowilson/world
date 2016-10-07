package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.Opener;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class OpenerManager {
    public static final String NAME = "opener";
    
    private static OpenerManager instance;
    
    private DAO<Opener> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private OpenerManager() {
        this.dao = DAOManager.getInstance().getDAO(Opener.class);
        
        int id = 1;
        for(Opener opener : this.getOpeners()) {
            for(String example : opener.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = opener.name;
                pair.url = "javascript:jumpTo('opener_edit.jsp?id=" + opener.id + "')";
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
                
                for(Opener opener : getOpeners()) {
                    boolean found = opener.name.contains(text) || opener.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = opener.id;
                        content.name = opener.name;
                        content.description = opener.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static OpenerManager getInstance() {
        if(instance == null) {
            instance = new OpenerManager();
        }
        return instance;
    }
    
    public void createOpener(Opener opener) {
    }
    
    public Opener getOpener(int id) {
        Opener opener = this.dao.get(id);
        if(opener != null) {
            return opener;
        }
        else {
            return null;
        }
    }
    
    public List<Opener> getOpeners() {
        List<Opener> result = new ArrayList<Opener>();
        for(Opener opener : this.dao.getAll()) {
            result.add(opener);
        }
        return result;
    }
    
    public void updateOpener(Opener opener) {
    }
    
    public void deleteOpener(int id) {
    }
    
    public List<QuizPair> getOpenerQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

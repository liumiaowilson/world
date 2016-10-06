package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.BodyLanguage;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class BodyLanguageManager {
    public static final String NAME = "body_language";
    
    private static BodyLanguageManager instance;
    
    private DAO<BodyLanguage> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private BodyLanguageManager() {
        this.dao = DAOManager.getInstance().getDAO(BodyLanguage.class);
        
        int id = 1;
        for(BodyLanguage lang : this.getBodyLanguages()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "<img src=\"" + lang.image + "\"/>";
            pair.bottom = lang.indicator;
            pair.url = "javascript:jumpTo('body_language_edit.jsp?id=" + lang.id + "')";
            this.pairs.put(pair.id, pair);
        }
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(BodyLanguage lang : getBodyLanguages()) {
                    boolean found = lang.name.contains(text) || lang.indicator.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = lang.id;
                        content.name = lang.name;
                        content.description = lang.indicator;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static BodyLanguageManager getInstance() {
        if(instance == null) {
            instance = new BodyLanguageManager();
        }
        return instance;
    }
    
    public void createBodyLanguage(BodyLanguage lang) {
    }
    
    public BodyLanguage getBodyLanguage(int id) {
        BodyLanguage lang = this.dao.get(id);
        if(lang != null) {
            return lang;
        }
        else {
            return null;
        }
    }
    
    public List<BodyLanguage> getBodyLanguages() {
        List<BodyLanguage> result = new ArrayList<BodyLanguage>();
        for(BodyLanguage lang : this.dao.getAll()) {
            result.add(lang);
        }
        return result;
    }
    
    public void updateBodyLanguage(BodyLanguage lang) {
    }
    
    public void deleteBodyLanguage(int id) {
    }
    
    public List<QuizPair> getBodyLanguageQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

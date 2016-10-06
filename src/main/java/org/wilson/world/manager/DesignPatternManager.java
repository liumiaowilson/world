package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.DesignPattern;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class DesignPatternManager {
    public static final String NAME = "design_pattern";
    
    private static DesignPatternManager instance;
    
    private DAO<DesignPattern> dao = null;
    
    private List<List<QuizPair>> pairs = new ArrayList<List<QuizPair>>();
    
    @SuppressWarnings("unchecked")
    private DesignPatternManager() {
        this.dao = DAOManager.getInstance().getDAO(DesignPattern.class);
        
        this.initQuizPairs();
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(DesignPattern pattern : getDesignPatterns()) {
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
    
    private void initQuizPairs() {
        this.pairs.add(this.getDefinitionQuizPairs());
        this.pairs.add(this.getTypeQuizPairs());
        this.pairs.add(this.getImageQuizPairs());
    }
    
    private List<QuizPair> getDefinitionQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DesignPattern pattern : this.getDesignPatterns()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = pattern.definition;
            pair.bottom = pattern.name;
            pair.url = "javascript:jumpTo('design_pattern_edit.jsp?id=" + pattern.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getTypeQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DesignPattern pattern : this.getDesignPatterns()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = pattern.type;
            pair.bottom = pattern.name;
            pair.url = "javascript:jumpTo('design_pattern_edit.jsp?id=" + pattern.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getImageQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DesignPattern pattern : this.getDesignPatterns()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "<img src=\"" + pattern.image + "\"/>";
            pair.bottom = pattern.name;
            pair.url = "javascript:jumpTo('design_pattern_edit.jsp?id=" + pattern.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private int getNumOfQuizPairs() {
        int sum = 0;
        for(List<QuizPair> list : pairs) {
            sum += list.size();
        }
        return sum;
    }
    
    public static DesignPatternManager getInstance() {
        if(instance == null) {
            instance = new DesignPatternManager();
        }
        return instance;
    }
    
    public void createDesignPattern(DesignPattern pattern) {
    }
    
    public DesignPattern getDesignPattern(int id) {
        DesignPattern pattern = this.dao.get(id);
        if(pattern != null) {
            return pattern;
        }
        else {
            return null;
        }
    }
    
    public List<DesignPattern> getDesignPatterns() {
        List<DesignPattern> result = new ArrayList<DesignPattern>();
        for(DesignPattern pattern : this.dao.getAll()) {
            result.add(pattern);
        }
        return result;
    }
    
    public void updateDesignPattern(DesignPattern pattern) {
    }
    
    public void deleteDesignPattern(int id) {
    }
    
    public List<List<QuizPair>> getDesignPatternQuizPairs() {
        return this.pairs;
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.TrickRule;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class TrickRuleManager {
    public static final String NAME = "trick_rule";
    
    private static TrickRuleManager instance;
    
    private DAO<TrickRule> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private TrickRuleManager() {
        this.dao = DAOManager.getInstance().getDAO(TrickRule.class);
        
        int id = 1;
        for(TrickRule rule : this.getTrickRules()) {
            for(String example : rule.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = rule.name;
                pair.url = "javascript:jumpTo('trick_rule_edit.jsp?id=" + rule.id + "')";
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
                
                for(TrickRule rule : getTrickRules()) {
                    boolean found = rule.name.contains(text) || rule.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = rule.id;
                        content.name = rule.name;
                        content.description = rule.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static TrickRuleManager getInstance() {
        if(instance == null) {
            instance = new TrickRuleManager();
        }
        return instance;
    }
    
    public void createTrickRule(TrickRule rule) {
    }
    
    public TrickRule getTrickRule(int id) {
        TrickRule rule = this.dao.get(id);
        if(rule != null) {
            return rule;
        }
        else {
            return null;
        }
    }
    
    public List<TrickRule> getTrickRules() {
        List<TrickRule> result = new ArrayList<TrickRule>();
        for(TrickRule rule : this.dao.getAll()) {
            result.add(rule);
        }
        return result;
    }
    
    public void updateTrickRule(TrickRule rule) {
    }
    
    public void deleteTrickRule(int id) {
    }
    
    public List<QuizPair> getTrickRuleQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

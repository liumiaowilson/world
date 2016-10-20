package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.CodeRule;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class CodeRuleManager {
    public static final String NAME = "code_rule";
    
    private static CodeRuleManager instance;
    
    private DAO<CodeRule> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private CodeRuleManager() {
        this.dao = DAOManager.getInstance().getDAO(CodeRule.class);
        
        int id = 1;
        for(CodeRule rule : this.getCodeRules()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "<b>" + rule.topic + "</b>:<br/>" + rule.name;
            pair.bottom = rule.definition;
            pair.url = "javascript:jumpTo('code_rule_edit.jsp?id=" + rule.id + "')";
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
                
                for(CodeRule rule : getCodeRules()) {
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
    
    public static CodeRuleManager getInstance() {
        if(instance == null) {
            instance = new CodeRuleManager();
        }
        return instance;
    }
    
    public void createCodeRule(CodeRule rule) {
    }
    
    public CodeRule getCodeRule(int id) {
        CodeRule rule = this.dao.get(id);
        if(rule != null) {
            return rule;
        }
        else {
            return null;
        }
    }
    
    public List<CodeRule> getCodeRules() {
        List<CodeRule> result = new ArrayList<CodeRule>();
        for(CodeRule rule : this.dao.getAll()) {
            result.add(rule);
        }
        return result;
    }
    
    public void updateCodeRule(CodeRule rule) {
    }
    
    public void deleteCodeRule(int id) {
    }
    
    public List<QuizPair> getCodeRuleQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

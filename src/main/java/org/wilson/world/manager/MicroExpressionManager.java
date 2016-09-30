package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.MicroExpression;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class MicroExpressionManager {
    public static final String NAME = "micro_expression";
    
    private static MicroExpressionManager instance;
    
    private DAO<MicroExpression> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private MicroExpressionManager() {
        this.dao = DAOManager.getInstance().getDAO(MicroExpression.class);
        
        int id = 1;
        for(MicroExpression expression : this.getMicroExpressions()) {
            for(String example : expression.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "<img src=\"" + example + "\"/>";
                pair.bottom = expression.name;
                pair.url = "javascript:jumpTo('micro_expression_edit.jsp?id=" + expression.id + "')";
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
                
                for(MicroExpression expression : getMicroExpressions()) {
                    
                    boolean found = expression.name.contains(text) || expression.getDefinition().contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = expression.id;
                        content.name = expression.name;
                        content.description = expression.getDefinition();
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static MicroExpressionManager getInstance() {
        if(instance == null) {
            instance = new MicroExpressionManager();
        }
        return instance;
    }
    
    public void createMicroExpression(MicroExpression expression) {
    }
    
    public MicroExpression getMicroExpression(int id) {
        MicroExpression expression = this.dao.get(id);
        if(expression != null) {
            return expression;
        }
        else {
            return null;
        }
    }
    
    public List<MicroExpression> getMicroExpressions() {
        List<MicroExpression> result = new ArrayList<MicroExpression>();
        for(MicroExpression expression : this.dao.getAll()) {
            result.add(expression);
        }
        return result;
    }
    
    public void updateMicroExpression(MicroExpression expression) {
    }
    
    public void deleteMicroExpression(int id) {
    }
    
    public List<QuizPair> getMicroExpressionQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

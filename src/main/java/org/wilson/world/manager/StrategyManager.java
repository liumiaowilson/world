package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.model.Strategy;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.strategy.TripleThinkingEventListener;

public class StrategyManager {
    public static final String NAME = "strategy";
    
    private static StrategyManager instance;
    
    private DAO<Strategy> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private StrategyManager() {
        this.dao = DAOManager.getInstance().getDAO(Strategy.class);
        
        int id = 1;
        for(Strategy strategy : this.getStrategies()) {
            for(String example : strategy.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = strategy.name;
                pair.url = "javascript:jumpTo('strategy_edit.jsp?id=" + strategy.id + "')";
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
                
                for(Strategy strategy : getStrategies()) {
                    boolean found = strategy.name.contains(text) || strategy.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = strategy.id;
                        content.name = strategy.name;
                        content.description = strategy.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        EventManager.getInstance().registerListener(EventType.TripleThinking, new TripleThinkingEventListener());
    }
    
    public static StrategyManager getInstance() {
        if(instance == null) {
            instance = new StrategyManager();
        }
        return instance;
    }
    
    public void createStrategy(Strategy strategy) {
    }
    
    public Strategy getStrategy(int id) {
        Strategy strategy = this.dao.get(id);
        if(strategy != null) {
            return strategy;
        }
        else {
            return null;
        }
    }
    
    public List<Strategy> getStrategies() {
        List<Strategy> result = new ArrayList<Strategy>();
        for(Strategy strategy : this.dao.getAll()) {
            result.add(strategy);
        }
        return result;
    }
    
    public void updateStrategy(Strategy strategy) {
    }
    
    public void deleteStrategy(int id) {
    }
    
    public List<QuizPair> getStrategyQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
    
    public Strategy randomStrategy() {
        List<Strategy> strategies = this.getStrategies();
        if(strategies.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(strategies.size());
        return strategies.get(n);
    }
}

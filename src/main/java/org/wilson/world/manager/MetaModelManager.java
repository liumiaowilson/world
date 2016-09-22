package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.MetaModel;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class MetaModelManager {
    public static final String NAME = "meta_model";
    
    private static MetaModelManager instance;
    
    private DAO<MetaModel> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private MetaModelManager() {
        this.dao = DAOManager.getInstance().getDAO(MetaModel.class);
        
        int id = 1;
        for(MetaModel model : this.getMetaModels()) {
            for(Entry<String, String> entry : model.examples.entrySet()) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "<p><b>" + entry.getKey() + "</b></p><p>" + entry.getValue() + "</p>";
                pair.bottom = model.name;
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
                
                for(MetaModel model : getMetaModels()) {
                    boolean found = model.name.contains(text) || model.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = model.id;
                        content.name = model.name;
                        content.description = model.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static MetaModelManager getInstance() {
        if(instance == null) {
            instance = new MetaModelManager();
        }
        return instance;
    }
    
    public void createMetaModel(MetaModel model) {
    }
    
    public MetaModel getMetaModel(int id) {
        MetaModel model = this.dao.get(id);
        if(model != null) {
            return model;
        }
        else {
            return null;
        }
    }
    
    public List<MetaModel> getMetaModels() {
        List<MetaModel> result = new ArrayList<MetaModel>();
        for(MetaModel model : this.dao.getAll()) {
            result.add(model);
        }
        return result;
    }
    
    public void updateMetaModel(MetaModel model) {
    }
    
    public void deleteMetaModel(int id) {
    }
    
    public List<QuizPair> getMetaModelQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.MiltonModel;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class MiltonModelManager {
    public static final String NAME = "milton_model";
    
    private static MiltonModelManager instance;
    
    private DAO<MiltonModel> dao = null;
    
    private Map<Integer, QuizPair> pairs = new HashMap<Integer, QuizPair>();
    
    @SuppressWarnings("unchecked")
    private MiltonModelManager() {
        this.dao = DAOManager.getInstance().getDAO(MiltonModel.class);
        
        int id = 1;
        for(MiltonModel model : this.getMiltonModels()) {
            for(String example : model.examples) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = model.name;
                pair.url = "javascript:jumpTo('milton_model_edit.jsp?id=" + model.id + "')";
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
                
                for(MiltonModel model : getMiltonModels()) {
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
    
    public static MiltonModelManager getInstance() {
        if(instance == null) {
            instance = new MiltonModelManager();
        }
        return instance;
    }
    
    public void createMiltonModel(MiltonModel model) {
    }
    
    public MiltonModel getMiltonModel(int id) {
        MiltonModel model = this.dao.get(id);
        if(model != null) {
            return model;
        }
        else {
            return null;
        }
    }
    
    public List<MiltonModel> getMiltonModels() {
        List<MiltonModel> result = new ArrayList<MiltonModel>();
        for(MiltonModel model : this.dao.getAll()) {
            result.add(model);
        }
        return result;
    }
    
    public void updateMiltonModel(MiltonModel model) {
    }
    
    public void deleteMiltonModel(int id) {
    }
    
    public List<QuizPair> getMiltonModelQuizPairs() {
        return new ArrayList<QuizPair>(this.pairs.values());
    }
}

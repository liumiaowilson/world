package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Algorithm;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class AlgorithmManager implements ItemTypeProvider {
    public static final String NAME = "algorithms";
    
    private static AlgorithmManager instance;
    
    private DAO<Algorithm> dao = null;
    
    @SuppressWarnings("unchecked")
    private AlgorithmManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Algorithm.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Algorithm algorithm : getAlgorithms()) {
                    boolean found = algorithm.name.contains(text) || algorithm.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = algorithm.id;
                        content.name = algorithm.name;
                        content.description = algorithm.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AlgorithmManager getInstance() {
        if(instance == null) {
            instance = new AlgorithmManager();
        }
        return instance;
    }
    
    public void createAlgorithm(Algorithm algorithm) {
        ItemManager.getInstance().checkDuplicate(algorithm);
        
        this.dao.create(algorithm);
    }
    
    public Algorithm getAlgorithm(int id) {
        Algorithm algorithm = this.dao.get(id);
        if(algorithm != null) {
            return algorithm;
        }
        else {
            return null;
        }
    }
    
    public List<Algorithm> getAlgorithms() {
        List<Algorithm> result = new ArrayList<Algorithm>();
        for(Algorithm algorithm : this.dao.getAll()) {
            result.add(algorithm);
        }
        return result;
    }
    
    public void updateAlgorithm(Algorithm algorithm) {
        this.dao.update(algorithm);
    }
    
    public void deleteAlgorithm(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Algorithm;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Algorithm algorithm = (Algorithm)target;
        return String.valueOf(algorithm.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Algorithm algorithm = (Algorithm)target;
        return algorithm.name;
    }
}

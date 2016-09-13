package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.coldread.ColdReadQuizPair;
import org.wilson.world.dao.DAO;
import org.wilson.world.model.ColdRead;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ColdReadManager {
    public static final String NAME = "cold_read";
    
    private static ColdReadManager instance;
    
    private DAO<ColdRead> dao = null;
    
    private Map<Integer, ColdReadQuizPair> pairs = new HashMap<Integer, ColdReadQuizPair>();
    
    @SuppressWarnings("unchecked")
    private ColdReadManager() {
        this.dao = DAOManager.getInstance().getDAO(ColdRead.class);
        
        int id = 1;
        for(ColdRead coldread : this.getColdReads()) {
            for(String example : coldread.examples) {
                ColdReadQuizPair pair = new ColdReadQuizPair();
                pair.id = id++;
                pair.top = example;
                pair.bottom = coldread.name;
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
                
                for(ColdRead coldread : getColdReads()) {
                    boolean found = coldread.name.contains(text) || coldread.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = coldread.id;
                        content.name = coldread.name;
                        content.description = coldread.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ColdReadManager getInstance() {
        if(instance == null) {
            instance = new ColdReadManager();
        }
        return instance;
    }
    
    public void createColdRead(ColdRead coldread) {
    }
    
    public ColdRead getColdRead(int id) {
        ColdRead coldread = this.dao.get(id);
        if(coldread != null) {
            return coldread;
        }
        else {
            return null;
        }
    }
    
    public List<ColdRead> getColdReads() {
        List<ColdRead> result = new ArrayList<ColdRead>();
        for(ColdRead coldread : this.dao.getAll()) {
            result.add(coldread);
        }
        return result;
    }
    
    public void updateColdRead(ColdRead coldread) {
    }
    
    public void deleteColdRead(int id) {
    }
    
    public List<ColdReadQuizPair> getColdReadQuizPairs() {
        return new ArrayList<ColdReadQuizPair>(this.pairs.values());
    }
}

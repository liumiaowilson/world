package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.ZodiacSign;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.zodiac_sign.ZodiacSignQuizPair;

public class ZodiacSignManager {
    public static final String NAME = "zodiac_sign";
    
    private static ZodiacSignManager instance;
    
    private DAO<ZodiacSign> dao = null;
    
    private Map<Integer, ZodiacSignQuizPair> pairs = new HashMap<Integer, ZodiacSignQuizPair>();
    
    @SuppressWarnings("unchecked")
    private ZodiacSignManager() {
        this.dao = DAOManager.getInstance().getDAO(ZodiacSign.class);
        
        int id = 1;
        for(ZodiacSign sign : this.getZodiacSigns()) {
            ZodiacSignQuizPair pair = new ZodiacSignQuizPair();
            pair.id = id++;
            pair.top = sign.strengths;
            pair.bottom = sign.name;
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
                
                for(ZodiacSign sign : getZodiacSigns()) {
                    boolean found = sign.name.contains(text) || sign.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = sign.id;
                        content.name = sign.name;
                        content.description = sign.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ZodiacSignManager getInstance() {
        if(instance == null) {
            instance = new ZodiacSignManager();
        }
        return instance;
    }
    
    public void createZodiacSign(ZodiacSign sign) {
    }
    
    public ZodiacSign getZodiacSign(int id) {
        ZodiacSign sign = this.dao.get(id);
        if(sign != null) {
            return sign;
        }
        else {
            return null;
        }
    }
    
    public List<ZodiacSign> getZodiacSigns() {
        List<ZodiacSign> result = new ArrayList<ZodiacSign>();
        for(ZodiacSign sign : this.dao.getAll()) {
            result.add(sign);
        }
        return result;
    }
    
    public void updateZodiacSign(ZodiacSign sign) {
    }
    
    public void deleteZodiacSign(int id) {
    }
    
    public List<ZodiacSignQuizPair> getZodiacSignQuizPairs() {
        return new ArrayList<ZodiacSignQuizPair>(this.pairs.values());
    }
}

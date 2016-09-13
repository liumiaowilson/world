package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Personality;
import org.wilson.world.personality.PersonalityQuizPair;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PersonalityManager implements ItemTypeProvider {
    public static final String NAME = "personality";
    
    private static PersonalityManager instance;
    
    private DAO<Personality> dao = null;
    
    private Map<String, List<Personality>> personalities = new HashMap<String, List<Personality>>();
    
    @SuppressWarnings("unchecked")
    private PersonalityManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Personality.class);
        ((CachedDAO<Personality>)this.dao).getCache().addCacheListener(new CacheListener<Personality>(){

            @Override
            public void cachePut(Personality old, Personality v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                String tags = v.tags.trim();
                for(String tag : tags.split(",")) {
                    if(!StringUtils.isBlank(tag)) {
                        tag = tag.trim();
                        
                        List<Personality> ps = personalities.get(tag);
                        if(ps == null) {
                            ps = new ArrayList<Personality>();
                            personalities.put(tag, ps);
                        }
                        ps.add(v);
                    }
                }
            }

            @Override
            public void cacheDeleted(Personality v) {
                String tags = v.tags.trim();
                for(String tag : tags.split(",")) {
                    if(!StringUtils.isBlank(tag)) {
                        tag = tag.trim();
                        
                        List<Personality> ps = personalities.get(tag);
                        if(ps != null) {
                            ps.remove(v);
                        }
                    }
                }
            }

            @Override
            public void cacheLoaded(List<Personality> all) {
            }

            @Override
            public void cacheLoading(List<Personality> old) {
                PersonalityManager.this.personalities.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Personality personality : getPersonalities()) {
                    boolean found = personality.name.contains(text) || personality.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = personality.id;
                        content.name = personality.name;
                        content.description = personality.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PersonalityManager getInstance() {
        if(instance == null) {
            instance = new PersonalityManager();
        }
        return instance;
    }
    
    public void createPersonality(Personality personality) {
        ItemManager.getInstance().checkDuplicate(personality);
        
        this.dao.create(personality);
    }
    
    public Personality getPersonality(int id) {
        Personality personality = this.dao.get(id);
        if(personality != null) {
            return personality;
        }
        else {
            return null;
        }
    }
    
    public List<Personality> getPersonalities() {
        List<Personality> result = new ArrayList<Personality>();
        for(Personality personality : this.dao.getAll()) {
            result.add(personality);
        }
        return result;
    }
    
    public void updatePersonality(Personality personality) {
        this.dao.update(personality);
    }
    
    public void deletePersonality(int id) {
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
        return target instanceof Personality;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Personality personality = (Personality)target;
        return String.valueOf(personality.id);
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
        
        Personality personality = (Personality)target;
        return personality.name;
    }
    
    public List<String> getTags() {
        return new ArrayList<String>(this.personalities.keySet());
    }
    
    public List<Personality> getPersonalities(String tag) {
        if(StringUtils.isBlank(tag)) {
            return Collections.emptyList();
        }
        
        return this.personalities.get(tag);
    }
    
    public List<PersonalityQuizPair> getPersonalityQuizPairs() {
        List<PersonalityQuizPair> ret = new ArrayList<PersonalityQuizPair>();
        
        int i = 1;
        for(Entry<String, List<Personality>> entry : this.personalities.entrySet()) {
            String tag = entry.getKey();
            List<Personality> ps = entry.getValue();
            for(Personality p : ps) {
                PersonalityQuizPair pair = new PersonalityQuizPair();
                pair.id = i++;
                pair.top = tag;
                pair.bottom = p.name;
                ret.add(pair);
            }
        }
        
        return ret;
    }
    
    public Personality randomPersonality() {
        List<Personality> personalities = this.dao.getAll();
        if(personalities.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(personalities.size());
        return personalities.get(n);
    }
}

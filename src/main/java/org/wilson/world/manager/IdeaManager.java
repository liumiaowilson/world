package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.CacheProvider;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.NumOfIdeasMonitor;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Idea;

public class IdeaManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "idea";
    
    private static IdeaManager instance;
    
    private Map<Integer, Idea> cache = null;
    private DAO<Idea> dao = null;
    
    @SuppressWarnings("unchecked")
    private IdeaManager() {
        this.dao = DAOManager.getInstance().getDAO(Idea.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        CacheManager.getInstance().registerCacheProvider(this);
        
        int limit = ConfigManager.getInstance().getConfigAsInt("idea.num.limit", 50);
        MonitorManager.getInstance().registerMonitorParticipant(new NumOfIdeasMonitor(limit));
    }
    
    public static IdeaManager getInstance() {
        if(instance == null) {
            instance = new IdeaManager();
        }
        return instance;
    }
    
    private Map<Integer, Idea> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    public void createIdea(Idea idea) {
        this.dao.create(idea);
        
        //do not load the cache on creating as creating may need to be fast for the first time
        if(this.cache != null) {
            this.cache.put(idea.id, idea);
        }
    }
    
    public Idea getIdeaFromDB(int id) {
        return this.dao.get(id);
    }
    
    public Idea getIdea(int id) {
        Idea idea = getCache().get(id);
        if(idea != null) {
            return idea;
        }
        
        idea = getIdeaFromDB(id);
        if(idea != null) {
            getCache().put(idea.id, idea);
            return idea;
        }
        else {
            return null;
        }
    }
    
    public List<Idea> getIdeasFromDB() {
        return this.dao.getAll();
    }
    
    public List<Idea> getIdeas() {
        List<Idea> result = new ArrayList<Idea>();
        for(Idea idea : getCache().values()) {
            result.add(idea);
        }
        return result;
    }
    
    public void updateIdea(Idea idea) {
        this.dao.update(idea);

        getCache().put(idea.id, idea);
    }
    
    public void deleteIdea(int id) {
        this.dao.delete(id);

        getCache().remove(id);
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
        return target instanceof Idea;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Idea idea = (Idea)target;
        return String.valueOf(idea.id);
    }

    @Override
    public String getCacheProviderName() {
        return this.dao.getItemTableName();
    }

    @Override
    public void reloadCache() {
        List<Idea> ideas = getIdeasFromDB();
        cache = new HashMap<Integer, Idea>();
        for(Idea idea : ideas) {
            cache.put(idea.id, idea);
        }
    }

    @Override
    public boolean canPreload() {
        return true;
    }
}

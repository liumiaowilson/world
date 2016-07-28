package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaStarProvider;
import org.wilson.world.idea.NumOfIdeasMonitor;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Idea;

public class IdeaManager implements ItemTypeProvider {
    public static final String NAME = "idea";
    
    private static IdeaManager instance;
    
    private DAO<Idea> dao = null;
    
    @SuppressWarnings("unchecked")
    private IdeaManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Idea.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        StarManager.getInstance().registerStarProvider(new IdeaStarProvider());
        
        int limit = ConfigManager.getInstance().getConfigAsInt("idea.num.limit", 50);
        MonitorManager.getInstance().registerMonitorParticipant(new NumOfIdeasMonitor(limit));
    }
    
    public static IdeaManager getInstance() {
        if(instance == null) {
            instance = new IdeaManager();
        }
        return instance;
    }
    
    public void createIdea(Idea idea) {
        this.dao.create(idea);
    }
    
    public Idea getIdea(int id) {
        Idea idea = this.dao.get(id);
        if(idea != null) {
            return idea;
        }
        else {
            return null;
        }
    }
    
    public List<Idea> getIdeas() {
        List<Idea> result = new ArrayList<Idea>();
        for(Idea idea : this.dao.getAll()) {
            result.add(idea);
        }
        return result;
    }
    
    public void updateIdea(Idea idea) {
        this.dao.update(idea);
    }
    
    public void deleteIdea(int id) {
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
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public boolean isFrozen(Idea idea) {
        if(idea == null) {
            return false;
        }
        
        return this.getFronzeTaskIds().contains(idea.id);
    }
    
    public Set<Integer> getFronzeTaskIds() {
        Set<Integer> ret = new HashSet<Integer>();
        
        Cache<Integer, Idea> cache = ((CachedDAO<Idea>)this.dao).getCache();
        List<Integer> keys = cache.getKeys();
        Collections.sort(keys, new Comparator<Integer>(){

            @Override
            public int compare(Integer o1, Integer o2) {
                return -(o1 - o2);
            }
            
        });
        
        int num = ConfigManager.getInstance().getConfigAsInt("idea.frozen.num", 3);
        for(int i = 0; i < num; i++) {
            ret.add(keys.get(i));
        }
        
        return ret;
    }
}

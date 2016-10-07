package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.idea.IdeaStarProvider;
import org.wilson.world.idea.NumOfIdeasMonitor;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Idea;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.FormatUtils;

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
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Idea idea : getIdeas()) {
                    boolean found = idea.name.contains(text) || idea.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = idea.id;
                        content.name = idea.name;
                        content.description = idea.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static IdeaManager getInstance() {
        if(instance == null) {
            instance = new IdeaManager();
        }
        return instance;
    }
    
    public void createIdea(Idea idea) {
        ItemManager.getInstance().checkDuplicate(idea);
        
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
        
        Idea idea = (Idea)target;
        return idea.name;
    }
    
    public boolean isFrozen(Idea idea) {
        if(idea == null) {
            return false;
        }
        
        return this.getFronzeTaskIds().contains(idea.id);
    }
    
    public List<Idea> getAvailableIdeas() {
        List<Idea> ret = new ArrayList<Idea>();
        
        Set<Integer> ids = this.getFronzeTaskIds();
        for(Idea idea : this.getIdeas()) {
            if(ids.contains(idea.id)) {
                continue;
            }
            ret.add(idea);
        }
        
        return ret;
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
    
    public Idea randomIdea() {
        List<Idea> ideas = this.getIdeas();
        if(ideas == null || ideas.isEmpty()) {
            return null;
        }
        int n = DiceManager.getInstance().random(ideas.size());
        return ideas.get(n);
    }
    
    public Map<String, Double> getIdeaTypeStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        int total = ConfigManager.getInstance().getConfigAsInt("idea.num.limit", 50);
        int all = this.getIdeas().size();
        if(all > total) {
            total = all;
        }
        int frozen = ConfigManager.getInstance().getConfigAsInt("idea.frozen.num", 3);
        int active = all - frozen;
        int free = total - all;
        
        double active_pct = FormatUtils.getRoundedValue(active * 100.0 / total);
        double frozen_pct = FormatUtils.getRoundedValue(frozen * 100.0 / total);
        double free_pct = FormatUtils.getRoundedValue(free * 100.0 / total);
        
        ret.put("Active", active_pct);
        ret.put("Frozen", frozen_pct);
        ret.put("Free", free_pct);
        
        return ret;
    }
    
    public IdeaConverter getIdeaConverterByType(String type) {
        if(StringUtils.isBlank(type)) {
            return null;
        }
        
        IdeaConverter ret = null;
        for(IdeaConverter converter : IdeaConverterFactory.getInstance().getIdeaConverters()) {
            if(type.equals(converter.getName())) {
                ret = converter;
                break;
            }
        }
        
        return ret;
    }
    
    public Map<String, Double> getIdeaEventStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        Map<String, Integer> all = StatsManager.getInstance().getEventTypeStats();
        Map<String, Integer> data = new HashMap<String, Integer>();
        for(Entry<String, Integer> entry : all.entrySet()) {
            String type = entry.getKey();
            if(type.contains("Idea")) {
                data.put(type, entry.getValue());
            }
        }
        
        int sum = 0;
        for(Integer i : data.values()) {
            sum += i;
        }
        
        if(sum != 0) {
            for(Entry<String, Integer> entry : data.entrySet()) {
                String type = entry.getKey();
                double pct = FormatUtils.getRoundedValue(entry.getValue() * 100.0 / sum);
                ret.put(type, pct);
            }
        }
        
        return ret;
    }
}

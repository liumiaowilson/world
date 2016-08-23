package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Penalty;
import org.wilson.world.penalty.PenaltyTaskGenerator;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PenaltyManager implements ItemTypeProvider {
    public static final String NAME = "penalty";
    
    private static PenaltyManager instance;
    
    private DAO<Penalty> dao = null;
    
    @SuppressWarnings("unchecked")
    private PenaltyManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Penalty.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        TaskSeedManager.getInstance().addTaskGenerator(new PenaltyTaskGenerator());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Penalty penalty : getPenalties()) {
                    boolean found = penalty.name.contains(text) || penalty.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = penalty.id;
                        content.name = penalty.name;
                        content.description = penalty.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static PenaltyManager getInstance() {
        if(instance == null) {
            instance = new PenaltyManager();
        }
        return instance;
    }
    
    public void createPenalty(Penalty penalty) {
        ItemManager.getInstance().checkDuplicate(penalty);
        
        this.dao.create(penalty);
    }
    
    public Penalty getPenalty(int id) {
        Penalty penalty = this.dao.get(id);
        if(penalty != null) {
            return penalty;
        }
        else {
            return null;
        }
    }
    
    public List<Penalty> getPenalties() {
        List<Penalty> result = new ArrayList<Penalty>();
        for(Penalty penalty : this.dao.getAll()) {
            result.add(penalty);
        }
        return result;
    }
    
    public void updatePenalty(Penalty penalty) {
        this.dao.update(penalty);
    }
    
    public void deletePenalty(int id) {
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
        return target instanceof Penalty;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Penalty penalty = (Penalty)target;
        return String.valueOf(penalty.id);
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
        
        Penalty penalty = (Penalty)target;
        return penalty.name;
    }
    
    public int getMaxSeverity() {
        int max = -1;
        for(Penalty penalty : this.getPenalties()) {
            if(penalty.severity > max) {
                max = penalty.severity;
            }
        }
        
        return max;
    }
}

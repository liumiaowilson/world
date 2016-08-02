package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.HumorPattern;

public class HumorPatternManager implements ItemTypeProvider {
    public static final String NAME = "humor_pattern";
    
    private static HumorPatternManager instance;
    
    private DAO<HumorPattern> dao = null;
    
    @SuppressWarnings("unchecked")
    private HumorPatternManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(HumorPattern.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static HumorPatternManager getInstance() {
        if(instance == null) {
            instance = new HumorPatternManager();
        }
        return instance;
    }
    
    public void createHumorPattern(HumorPattern pattern) {
        this.dao.create(pattern);
    }
    
    public HumorPattern getHumorPattern(int id) {
        HumorPattern pattern = this.dao.get(id);
        if(pattern != null) {
            return pattern;
        }
        else {
            return null;
        }
    }
    
    public List<HumorPattern> getHumorPatterns() {
        List<HumorPattern> result = new ArrayList<HumorPattern>();
        for(HumorPattern pattern : this.dao.getAll()) {
            result.add(pattern);
        }
        return result;
    }
    
    public void updateHumorPattern(HumorPattern pattern) {
        this.dao.update(pattern);
    }
    
    public void deleteHumorPattern(int id) {
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
        return target instanceof HumorPattern;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        HumorPattern pattern = (HumorPattern)target;
        return String.valueOf(pattern.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

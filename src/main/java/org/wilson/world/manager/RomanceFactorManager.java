package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.RomanceFactor;

public class RomanceFactorManager implements ItemTypeProvider {
    public static final String NAME = "romance_factor";
    
    private static RomanceFactorManager instance;
    
    private DAO<RomanceFactor> dao = null;
    
    @SuppressWarnings("unchecked")
    private RomanceFactorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(RomanceFactor.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static RomanceFactorManager getInstance() {
        if(instance == null) {
            instance = new RomanceFactorManager();
        }
        return instance;
    }
    
    public void createRomanceFactor(RomanceFactor factor) {
        this.dao.create(factor);
    }
    
    public RomanceFactor getRomanceFactor(int id) {
        RomanceFactor factor = this.dao.get(id);
        if(factor != null) {
            return factor;
        }
        else {
            return null;
        }
    }
    
    public List<RomanceFactor> getRomanceFactors() {
        List<RomanceFactor> result = new ArrayList<RomanceFactor>();
        for(RomanceFactor factor : this.dao.getAll()) {
            result.add(factor);
        }
        return result;
    }
    
    public void updateRomanceFactor(RomanceFactor factor) {
        this.dao.update(factor);
    }
    
    public void deleteRomanceFactor(int id) {
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
        return target instanceof RomanceFactor;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        RomanceFactor factor = (RomanceFactor)target;
        return String.valueOf(factor.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

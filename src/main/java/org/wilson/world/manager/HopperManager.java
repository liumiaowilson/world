package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Hopper;

public class HopperManager implements ItemTypeProvider {
    public static final String NAME = "hopper";
    
    private static HopperManager instance;
    
    private DAO<Hopper> dao = null;
    
    @SuppressWarnings("unchecked")
    private HopperManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Hopper.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static HopperManager getInstance() {
        if(instance == null) {
            instance = new HopperManager();
        }
        return instance;
    }
    
    public Cache<Integer, Hopper> getCache() {
        return ((CachedDAO<Hopper>)this.dao).getCache();
    }
    
    public void createHopper(Hopper hopper) {
        this.dao.create(hopper);
    }
    
    public Hopper getHopper(int id) {
        Hopper hopper = this.dao.get(id);
        if(hopper != null) {
            return hopper;
        }
        else {
            return null;
        }
    }
    
    public List<Hopper> getHoppers() {
        List<Hopper> result = new ArrayList<Hopper>();
        for(Hopper hopper : this.dao.getAll()) {
            result.add(hopper);
        }
        return result;
    }
    
    public void updateHopper(Hopper hopper) {
        this.dao.update(hopper);
    }
    
    public void deleteHopper(int id) {
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
        return target instanceof Hopper;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Hopper hopper = (Hopper)target;
        return String.valueOf(hopper.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

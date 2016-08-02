package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Humor;

public class HumorManager implements ItemTypeProvider {
    public static final String NAME = "humor";
    
    private static HumorManager instance;
    
    private DAO<Humor> dao = null;
    
    @SuppressWarnings("unchecked")
    private HumorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Humor.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static HumorManager getInstance() {
        if(instance == null) {
            instance = new HumorManager();
        }
        return instance;
    }
    
    public void createHumor(Humor humor) {
        this.dao.create(humor);
    }
    
    public Humor getHumor(int id) {
        Humor humor = this.dao.get(id);
        if(humor != null) {
            return humor;
        }
        else {
            return null;
        }
    }
    
    public List<Humor> getHumors() {
        List<Humor> result = new ArrayList<Humor>();
        for(Humor humor : this.dao.getAll()) {
            result.add(humor);
        }
        return result;
    }
    
    public void updateHumor(Humor humor) {
        this.dao.update(humor);
    }
    
    public void deleteHumor(int id) {
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
        return target instanceof Humor;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Humor humor = (Humor)target;
        return String.valueOf(humor.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

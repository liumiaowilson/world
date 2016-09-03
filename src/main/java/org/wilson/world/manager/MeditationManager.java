package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Meditation;

public class MeditationManager implements ItemTypeProvider {
    public static final String NAME = "meditation";
    
    private static MeditationManager instance;
    
    private DAO<Meditation> dao = null;
    
    @SuppressWarnings("unchecked")
    private MeditationManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Meditation.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static MeditationManager getInstance() {
        if(instance == null) {
            instance = new MeditationManager();
        }
        return instance;
    }
    
    public void createMeditation(Meditation meditation) {
        this.dao.create(meditation);
    }
    
    public Meditation getMeditation(int id) {
        Meditation meditation = this.dao.get(id);
        if(meditation != null) {
            return meditation;
        }
        else {
            return null;
        }
    }
    
    public List<Meditation> getMeditations() {
        List<Meditation> result = new ArrayList<Meditation>();
        for(Meditation meditation : this.dao.getAll()) {
            result.add(meditation);
        }
        return result;
    }
    
    public void updateMeditation(Meditation meditation) {
        this.dao.update(meditation);
    }
    
    public void deleteMeditation(int id) {
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
        return target instanceof Meditation;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Meditation meditation = (Meditation)target;
        return String.valueOf(meditation.id);
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
        
        Meditation meditation = (Meditation)target;
        return String.valueOf(meditation.id);
    }
}

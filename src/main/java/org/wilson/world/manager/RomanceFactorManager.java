package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Detail;
import org.wilson.world.model.RomanceFactor;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RomanceFactorManager implements ItemTypeProvider {
    public static final String NAME = "romance_factor";
    
    private static RomanceFactorManager instance;
    
    private DAO<RomanceFactor> dao = null;
    
    @SuppressWarnings("unchecked")
    private RomanceFactorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(RomanceFactor.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(RomanceFactor factor : getRomanceFactors()) {
                    boolean found = factor.name.contains(text) || factor.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = factor.id;
                        content.name = factor.name;
                        content.description = factor.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RomanceFactorManager getInstance() {
        if(instance == null) {
            instance = new RomanceFactorManager();
        }
        return instance;
    }
    
    public void createRomanceFactor(RomanceFactor factor) {
        ItemManager.getInstance().checkDuplicate(factor);
        
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<RomanceFactor> randomRomanceFactors() {
        List<RomanceFactor> ret = new ArrayList<RomanceFactor>();
        
        List<RomanceFactor> all = this.getRomanceFactors();
        boolean include = ConfigManager.getInstance().getConfigAsBoolean("romance_factor.include.detail", true);
        if(include) {
            for(Detail detail : DetailManager.getInstance().getDetails()) {
                RomanceFactor factor = new RomanceFactor();
                factor.name = detail.name;
                factor.content = detail.content;
                all.add(factor);
            }
        }
        
        if(all.isEmpty()) {
            return ret;
        }
        Collections.shuffle(all);
        int n = DiceManager.getInstance().random(all.size() - 1);
        for(int i = 0; i < n + 1; i++) {
            ret.add(all.get(i));
        }
        
        return ret;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        RomanceFactor factor = (RomanceFactor)target;
        return factor.name;
    }
}

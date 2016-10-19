package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TongueTwister;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class TongueTwisterManager implements ItemTypeProvider {
    public static final String NAME = "tongue_twister";
    
    private static TongueTwisterManager instance;
    
    private DAO<TongueTwister> dao = null;
    
    @SuppressWarnings("unchecked")
    private TongueTwisterManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TongueTwister.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(TongueTwister tt : getTongueTwisters()) {
                    boolean found = tt.name.contains(text) || tt.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = tt.id;
                        content.name = tt.name;
                        content.description = tt.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static TongueTwisterManager getInstance() {
        if(instance == null) {
            instance = new TongueTwisterManager();
        }
        return instance;
    }
    
    public void createTongueTwister(TongueTwister tt) {
        ItemManager.getInstance().checkDuplicate(tt);
        
        this.dao.create(tt);
    }
    
    public TongueTwister getTongueTwister(int id) {
        TongueTwister tt = this.dao.get(id);
        if(tt != null) {
            return tt;
        }
        else {
            return null;
        }
    }
    
    public List<TongueTwister> getTongueTwisters() {
        List<TongueTwister> result = new ArrayList<TongueTwister>();
        for(TongueTwister tt : this.dao.getAll()) {
            result.add(tt);
        }
        return result;
    }
    
    public void updateTongueTwister(TongueTwister tt) {
        this.dao.update(tt);
    }
    
    public void deleteTongueTwister(int id) {
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
        return target instanceof TongueTwister;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TongueTwister tt = (TongueTwister)target;
        return String.valueOf(tt.id);
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
        
        TongueTwister tt = (TongueTwister)target;
        return tt.name;
    }
    
    public TongueTwister randomTongueTwister() {
        List<TongueTwister> tts = this.getTongueTwisters();
        if(tts.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(tts.size());
        return tts.get(n);
    }
}

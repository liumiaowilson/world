package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.HopperData;

public class HopperDataManager implements ItemTypeProvider {
    public static final String NAME = "hopper_data";
    
    private static HopperDataManager instance;
    
    private DAO<HopperData> dao = null;
    
    private Cache<Integer, HopperData> cache = null;
    
    @SuppressWarnings("unchecked")
    private HopperDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(HopperData.class);
        this.cache = new DefaultCache<Integer, HopperData>("hopper_data_manager_cache", false);
        ((CachedDAO<HopperData>)this.dao).getCache().addCacheListener(new CacheListener<HopperData>(){

            @Override
            public void cachePut(HopperData old, HopperData v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                HopperDataManager.this.cache.put(v.hopperId, v);
            }

            @Override
            public void cacheDeleted(HopperData v) {
                HopperDataManager.this.cache.delete(v.hopperId);
            }

            @Override
            public void cacheLoaded(List<HopperData> all) {
            }

            @Override
            public void cacheLoading(List<HopperData> old) {
                HopperDataManager.this.cache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
    }
    
    public static HopperDataManager getInstance() {
        if(instance == null) {
            instance = new HopperDataManager();
        }
        return instance;
    }
    
    public void createHopperData(HopperData data) {
        this.dao.create(data);
    }
    
    public HopperData getHopperData(int id) {
        HopperData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<HopperData> getHopperDatas() {
        List<HopperData> result = new ArrayList<HopperData>();
        for(HopperData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateHopperData(HopperData data) {
        this.dao.update(data);
    }
    
    public void deleteHopperDataa(int id) {
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
        return target instanceof HopperData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        HopperData data = (HopperData)target;
        return String.valueOf(data.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public HopperData getHopperDataByHopperId(int hopperId) {
        return this.cache.get(hopperId);
    }
}
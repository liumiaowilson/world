package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.CacheProvider;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ActionParam;

public class ActionParamManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "action_param";
    
    private static ActionParamManager instance;
    
    private Map<Integer, ActionParam> cache = null;
    
    private DAO<ActionParam> dao = null;
    
    @SuppressWarnings("unchecked")
    private ActionParamManager() {
        this.dao = DAOManager.getInstance().getDAO(ActionParam.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static ActionParamManager getInstance() {
        if(instance == null) {
            instance = new ActionParamManager();
        }
        return instance;
    }
    
    private Map<Integer, ActionParam> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    public void createActionParam(ActionParam param) {
        this.dao.create(param);
        this.getCache().put(param.id, param);
    }
    
    public ActionParam getActionParamFromDB(int id) {
        return this.dao.get(id);
    }
    
    public ActionParam getActionParam(int id) {
        ActionParam param = getCache().get(id);
        if(param != null) {
            return param;
        }
        
        param = getActionParamFromDB(id);
        if(param != null) {
            getCache().put(param.id, param);
            return param;
        }
        else {
            return null;
        }
    }
    
    public List<ActionParam> getActionParamsFromDB() {
        return this.dao.getAll();
    }
    
    public List<ActionParam> getActionParams() {
        List<ActionParam> result = new ArrayList<ActionParam>();
        for(ActionParam param : getCache().values()) {
            result.add(param);
        }
        return result;
    }
    
    public void updateActionParam(ActionParam param) {
        this.dao.update(param);
        this.getCache().put(param.id, param);
    }
    
    public void deleteActionParam(int id) {
        this.dao.delete(id);
        this.getCache().remove(id);
    }
    
    public List<ActionParam> getActionParamsByActionId(int actionId) {
        List<ActionParam> ret = new ArrayList<ActionParam>();
        for(ActionParam param : this.getCache().values()) {
            if(param.actionId == actionId) {
                ret.add(param);
            }
        }
        Collections.sort(ret, new Comparator<ActionParam>(){
            @Override
            public int compare(ActionParam o1, ActionParam o2) {
                return o1.id - o2.id;
            }
        });
        return ret;
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
        return target instanceof ActionParam;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ActionParam param = (ActionParam)target;
        return String.valueOf(param.id);
    }

    @Override
    public String getCacheProviderName() {
        return this.dao.getItemTableName();
    }

    @Override
    public void reloadCache() {
        List<ActionParam> params = getActionParamsFromDB();
        cache = new HashMap<Integer, ActionParam>();
        for(ActionParam param : params) {
            cache.put(param.id, param);
        }
    }

    @Override
    public boolean canPreload() {
        return true;
    }
}

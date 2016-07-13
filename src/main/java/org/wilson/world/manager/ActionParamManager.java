package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ActionParam;

public class ActionParamManager implements ItemTypeProvider {
    public static final String NAME = "action_param";
    
    private static ActionParamManager instance;
    
    private DAO<ActionParam> dao = null;
    
    @SuppressWarnings("unchecked")
    private ActionParamManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ActionParam.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ActionParamManager getInstance() {
        if(instance == null) {
            instance = new ActionParamManager();
        }
        return instance;
    }
    
    public void createActionParam(ActionParam param) {
        this.dao.create(param);
    }
    
    public ActionParam getActionParamFromDB(int id) {
        return this.dao.get(id);
    }
    
    public ActionParam getActionParam(int id) {
        ActionParam param = this.dao.get(id);
        if(param != null) {
            return param;
        }
        else {
            return null;
        }
    }
    
    public List<ActionParam> getActionParams() {
        return this.dao.getAll();
    }
    
    public void updateActionParam(ActionParam param) {
        this.dao.update(param);
    }
    
    public void deleteActionParam(int id) {
        this.dao.delete(id);
    }
    
    public List<ActionParam> getActionParamsByActionId(int actionId) {
        List<ActionParam> ret = new ArrayList<ActionParam>();
        for(ActionParam param : this.dao.getAll()) {
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
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

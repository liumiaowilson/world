package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.dao.ActionDAO.ActionQueryByNameTemplate;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Action;
import org.wilson.world.model.ActionParam;
import org.wilson.world.model.ExtensionPoint;

public class ActionManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "action";
    
    private static final Logger logger = Logger.getLogger(ActionManager.class);
    
    private static ActionManager instance;
    
    private Map<Integer, Action> cache = null;
    
    private DAO<Action> dao = null;
    
    @SuppressWarnings("unchecked")
    private ActionManager() {
        this.dao = DAOManager.getInstance().getDAO(Action.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static ActionManager getInstance() {
        if(instance == null) {
            instance = new ActionManager();
        }
        return instance;
    }
    
    private Map<Integer, Action> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    public void createAction(Action action) {
        dao.create(action);
        
        for(ActionParam param : action.params) {
            param.actionId = action.id;
            ActionParamManager.getInstance().createActionParam(param);
        }
        
        if(action.id != 0) {
            this.getCache().put(action.id, action);
        }
    }
    
    public Action getActionFromDBByName(String name) {
        QueryTemplate qt = this.dao.getQueryTemplate(ActionQueryByNameTemplate.NAME);
        List<Action> actions = this.dao.query(qt, name);
        if(actions.isEmpty()) {
            return null;
        }
        else {
            return actions.get(0);
        }
    }
    
    public Action getActionFromDB(int id) {
        return dao.get(id);
    }
    
    public Action getAction(int id) {
        Action action = getCache().get(id);
        if(action != null) {
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            return action;
        }
        
        action = getActionFromDB(id);
        if(action != null) {
            getCache().put(action.id, action);
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            return action;
        }
        else {
            return null;
        }
    }
    
    public Action getAction(String name) {
        for(Action action : getCache().values()) {
            if(action.name.equals(name)) {
                action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
                return action;
            }
        }
        
        Action action = getActionFromDBByName(name);
        if(action != null) {
            getCache().put(action.id, action);
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            return action;
        }
        else {
            return null;
        }
    }
    
    public List<Action> getActionsFromDB() {
        return dao.getAll();
    }
    
    public List<Action> getActions() {
        List<Action> result = new ArrayList<Action>();
        for(Action action : getCache().values()) {
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            result.add(action);
        }
        return result;
    }
    
    private boolean hasActionParam(List<ActionParam> params, ActionParam param) {
        for(ActionParam p : params) {
            if(p.id == param.id) {
                return true;
            }
        }
        return false;
    }
    
    public void updateAction(Action action) {
        this.dao.update(action);
        
        List<ActionParam> oldParams = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
        List<ActionParam> create = new ArrayList<ActionParam>();
        List<ActionParam> update = new ArrayList<ActionParam>();
        List<ActionParam> delete = new ArrayList<ActionParam>();
        for(ActionParam p : action.params) {
            if(p.id == 0) {
                create.add(p);
            }
            else if(hasActionParam(oldParams, p)) {
                update.add(p);
            }
            else {
                delete.add(p);
            }
        }
        for(ActionParam p : oldParams) {
            if(!hasActionParam(action.params, p)) {
                delete.add(p);
            }
        }
        
        for(ActionParam param : create) {
            param.actionId = action.id;
            ActionParamManager.getInstance().createActionParam(param);
        }
        
        for(ActionParam param : update) {
            param.actionId = action.id;
            ActionParamManager.getInstance().updateActionParam(param);
        }
        
        for(ActionParam param : delete) {
            ActionParamManager.getInstance().deleteActionParam(param.id);
        }
        
        getCache().put(action.id, action);
    }
    
    public void deleteAction(int id) {
        Action oldAction = this.getAction(id);
        
        for(ActionParam param : oldAction.params) {
            ActionParamManager.getInstance().deleteActionParam(param.id);
        }
        
        this.dao.delete(id);
        
        getCache().remove(id);
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
        return target instanceof Action;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Action action = (Action)target;
        return String.valueOf(action.id);
    }

    @Override
    public String getCacheProviderName() {
        return this.dao.getItemTableName();
    }

    @Override
    public void reloadCache() {
        List<Action> actions = getActionsFromDB();
        cache = new HashMap<Integer, Action>();
        for(Action action : actions) {
            cache.put(action.id, action);
        }
    }

    @Override
    public boolean canPreload() {
        return true;
    }
    
    public Object run(String actionName, Map<String, Object> context) {
        if(StringUtils.isBlank(actionName)) {
            return null;
        }
        
        Action action = this.getAction(actionName);
        return run(action, context);
    }
    
    public Object run(Action action, Map<String, Object> context) {
        if(action == null) {
            return null;
        }
        
        StringBuffer sb = new StringBuffer("");
        for(ActionParam param : action.params) {
            String name = param.name;
            String defaultValue = param.defaultValue;
            sb.append("if(typeof " + name + " == 'undefined') {\n");
            sb.append("    " + name + " = eval('" + defaultValue + "');\n");
            sb.append("}\n");
            sb.append("\n");
        }
        
        String script = sb.toString() + action.script;
        Object ret = ScriptManager.getInstance().run(script, context);
        return ret;
    }
    
    public Object dryRun(String actionName, Map<String, String> dryRunContext) {
        if(StringUtils.isBlank(actionName)) {
            return null;
        }
        
        Action action = this.getAction(actionName);
        Action dryRunAction = new Action();
        dryRunAction.id = action.id;
        dryRunAction.name = action.name;
        dryRunAction.script = action.script;
        for(Entry<String, String> entry : dryRunContext.entrySet()) {
            ActionParam param = new ActionParam();
            param.actionId = dryRunAction.id;
            param.name = entry.getKey();
            param.defaultValue = entry.getValue();
            dryRunAction.params.add(param);
        }
        
        return run(dryRunAction, null);
    }
    
    @SuppressWarnings("rawtypes")
    public Action generateAction(String extName) {
        if(StringUtils.isBlank(extName)) {
            return null;
        }
        
        ExtensionPoint ep = ExtManager.getInstance().getExtensionPoint(extName);
        if(ep == null) {
            logger.warn("Failed to find extension point with name [" + extName + "].");
            return null;
        }
        
        Action action = new Action();
        action.name = extName;
        
        for(String name : ep.paramNames) {
            Class clazz = ep.params.get(name);
            String defaultValue = getDefaultValue(clazz);
            ActionParam param = new ActionParam();
            param.name = name;
            param.defaultValue = defaultValue;
            action.params.add(param);
        }
        
        action.script = this.getDefaultValue(ep.returnType);
        
        return action;
    }
    
    @SuppressWarnings("rawtypes")
    private String getDefaultValue(Class clazz) {
        if(clazz == null) {
            return null;
        }
        if(clazz == boolean.class) {
            return "false";
        }
        else if(clazz == int.class) {
            return "0";
        }
        else if(clazz == double.class) {
            return "0.0";
        }
        else {
            return "null";
        }
    }
}

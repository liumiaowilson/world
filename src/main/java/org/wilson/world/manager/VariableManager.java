package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Variable;

public class VariableManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(VariableManager.class);
    
    public static final String NAME = "variable";
    
    private static VariableManager instance;
    
    private DAO<Variable> dao = null;
    
    private Cache<String, Object> cache = null;
    
    @SuppressWarnings("unchecked")
    private VariableManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Variable.class);
        this.cache = new DefaultCache<String, Object>("variable_manager_cache", false);
        ((CachedDAO<Variable>)this.dao).getCache().addCacheListener(new CacheListener<Variable>(){

            @Override
            public void cachePut(Variable old, Variable v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                try {
                    String expression = v.expression;
                    Object ret = ScriptManager.getInstance().run(expression);
                    VariableManager.this.cache.put(v.name, ret);
                    ScriptManager.getInstance().addBinding(v.name, ret);
                }
                catch(Exception e) {
                    logger.warn("Failed to load variable [" + v.name + "]");
                }
            }

            @Override
            public void cacheDeleted(Variable v) {
                VariableManager.this.cache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<Variable> all) {
            }

            @Override
            public void cacheLoading(List<Variable> old) {
                VariableManager.this.cache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static VariableManager getInstance() {
        if(instance == null) {
            instance = new VariableManager();
        }
        return instance;
    }
    
    public void createVariable(Variable variable) {
        ItemManager.getInstance().checkDuplicate(variable);
        
        this.dao.create(variable);
    }
    
    public Variable getVariable(int id) {
        Variable variable = this.dao.get(id);
        if(variable != null) {
            return variable;
        }
        else {
            return null;
        }
    }
    
    public List<Variable> getVariables() {
        List<Variable> result = new ArrayList<Variable>();
        for(Variable variable : this.dao.getAll()) {
            result.add(variable);
        }
        return result;
    }
    
    public void updateVariable(Variable variable) {
        this.dao.update(variable);
    }
    
    public void deleteVariable(int id) {
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
        return target instanceof Variable;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Variable variable = (Variable)target;
        return String.valueOf(variable.id);
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
        
        Variable variable = (Variable)target;
        return variable.name;
    }
}

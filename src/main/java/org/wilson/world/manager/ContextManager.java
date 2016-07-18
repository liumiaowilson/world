package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Context;

public class ContextManager implements ItemTypeProvider {
    public static final String NAME = "context";
    
    private Context currentContext;
    
    private static ContextManager instance;
    
    private DAO<Context> dao = null;
    
    @SuppressWarnings("unchecked")
    private ContextManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Context.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ContextManager getInstance() {
        if(instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }
    
    public void setCurrentContext(Context context) {
        this.currentContext = context;
    }
    
    public Context getCurrentContext() {
        return this.currentContext;
    }
    
    public void createContext(Context context) {
        this.dao.create(context);
    }
    
    public Context getContext(int id) {
        Context context = this.dao.get(id);
        if(context != null) {
            return context;
        }
        else {
            return null;
        }
    }
    
    public Context getContext(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        for(Context context : this.dao.getAll()) {
            if(context.name.equals(name)) {
                return context;
            }
        }
        return null;
    }
    
    public List<Context> getContexts() {
        List<Context> result = new ArrayList<Context>();
        for(Context context : this.dao.getAll()) {
            result.add(context);
        }
        return result;
    }
    
    public void updateContext(Context context) {
        this.dao.update(context);
    }
    
    public void deleteContext(int id) {
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
        return target instanceof Context;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Context context = (Context)target;
        return String.valueOf(context.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

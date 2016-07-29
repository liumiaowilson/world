package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Variable;

public class VariableManager implements ItemTypeProvider {
    public static final String NAME = "variable";
    
    private static VariableManager instance;
    
    private DAO<Variable> dao = null;
    
    @SuppressWarnings("unchecked")
    private VariableManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Variable.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static VariableManager getInstance() {
        if(instance == null) {
            instance = new VariableManager();
        }
        return instance;
    }
    
    public void createVariable(Variable variable) {
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

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}

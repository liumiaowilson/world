package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.SkillData;
import org.wilson.world.skill.SkillScope;
import org.wilson.world.skill.SkillTarget;
import org.wilson.world.skill.SkillType;

public class SkillDataManager implements ItemTypeProvider {
    public static final String NAME = "skill_data";
    
    private static SkillDataManager instance;
    
    private DAO<SkillData> dao = null;
    
    private List<String> skillTypes = new ArrayList<String>();
    private List<String> skillScopes = new ArrayList<String>();
    private List<String> skillTargets = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private SkillDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(SkillData.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        for(SkillType type : SkillType.values()) {
            skillTypes.add(type.name());
        }
        
        for(SkillScope scope : SkillScope.values()) {
            skillScopes.add(scope.name());
        }
        
        for(SkillTarget target : SkillTarget.values()) {
            skillTargets.add(target.name());
        }
    }
    
    public static SkillDataManager getInstance() {
        if(instance == null) {
            instance = new SkillDataManager();
        }
        return instance;
    }
    
    public void createSkillData(SkillData data) {
        this.dao.create(data);
    }
    
    public SkillData getSkillData(int id) {
        SkillData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<SkillData> getSkillDatas() {
        List<SkillData> result = new ArrayList<SkillData>();
        for(SkillData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateSkillData(SkillData data) {
        this.dao.update(data);
    }
    
    public void deleteSkillData(int id) {
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
        return target instanceof SkillData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        SkillData data = (SkillData)target;
        return String.valueOf(data.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<String> getSkillTypes() {
        return this.skillTypes;
    }
    
    public List<String> getSkillScopes() {
        return this.skillScopes;
    }
    
    public List<String> getSkillTargets() {
        return this.skillTargets;
    }
}

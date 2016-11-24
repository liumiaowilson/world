package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.SkillData;
import org.wilson.world.skill.DefaultSkill;
import org.wilson.world.skill.Skill;
import org.wilson.world.skill.SkillCanTrigger;
import org.wilson.world.skill.SkillFactory;
import org.wilson.world.skill.SkillScope;
import org.wilson.world.skill.SkillTarget;
import org.wilson.world.skill.SkillTrigger;
import org.wilson.world.skill.SkillType;
import org.wilson.world.skill.SystemSkill;

public class SkillDataManager implements ItemTypeProvider {
    public static final String NAME = "skill_data";
    
    private static SkillDataManager instance;
    
    private DAO<SkillData> dao = null;
    
    private List<String> skillTypes = new ArrayList<String>();
    private List<String> skillScopes = new ArrayList<String>();
    private List<String> skillTargets = new ArrayList<String>();
    
    private Cache<Integer, Skill> cache = null;
    
    private static int GLOBAL_ID = 1;
    
    @SuppressWarnings("unchecked")
    private SkillDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(SkillData.class);
        this.cache = new DefaultCache<Integer, Skill>("skill_data_manager_cache", false);
        ((CachedDAO<SkillData>)this.dao).getCache().addCacheListener(new CacheListener<SkillData>(){

            @Override
            public void cachePut(SkillData old, SkillData v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                loadSkillData(v);
            }

            @Override
            public void cacheDeleted(SkillData v) {
                SkillDataManager.this.cache.delete(v.id);
            }

            @Override
            public void cacheLoaded(List<SkillData> all) {
                loadSystemSkills();
            }

            @Override
            public void cacheLoading(List<SkillData> old) {
                SkillDataManager.this.cache.clear();
            }
            
        });
        
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
    
    private void loadSystemSkills() {
        GLOBAL_ID = 1;
        
        for(Skill skill : SkillFactory.getInstance().getSkills()) {
            this.loadSystemSkill(skill);
        }
    }
    
    private void loadSystemSkill(Skill skill) {
        if(skill == null) {
            return;
        }
        
        skill.setId(-GLOBAL_ID++);
        this.cache.put(skill.getId(), skill);
    }
    
    private void loadSkillData(SkillData data) {
        if(data == null) {
            return;
        }
        
        String canTriggerImpl = data.canTrigger;
        SkillCanTrigger canTrigger = (SkillCanTrigger) ExtManager.getInstance().getExtension(canTriggerImpl, SkillCanTrigger.class);
        
        String triggerImpl = data.trigger;
        SkillTrigger trigger = (SkillTrigger) ExtManager.getInstance().getExtension(triggerImpl, SkillTrigger.class);
        
        DefaultSkill skill = new DefaultSkill(data, canTrigger, trigger);
        this.cache.put(skill.getId(), skill);
    }
    
    public void createSkillData(SkillData data) {
        ItemManager.getInstance().checkDuplicate(data);
        
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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
    
    public List<Skill> getSkills() {
        return this.cache.getAll();
    }
    
    public List<Skill> getSystemSkills() {
        List<Skill> ret = new ArrayList<Skill>();
        
        for(Skill skill : this.getSkills()) {
            if(skill instanceof SystemSkill) {
                ret.add(skill);
            }
        }
        
        return ret;
    }
    
    public Skill getSkill(int id) {
        return this.cache.get(id);
    }
    
    public Skill randomSkill() {
        List<Skill> skills = this.getSkills();
        if(skills.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(skills.size());
        return skills.get(n);
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        SkillData data = (SkillData)target;
        return data.name;
    }
}

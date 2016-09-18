package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.behavior.BehaviorDBCleaner;
import org.wilson.world.behavior.BehaviorFrequency;
import org.wilson.world.behavior.IBehaviorDef;
import org.wilson.world.behavior.PurgeBehavJob;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Behavior;
import org.wilson.world.model.BehaviorDef;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;

public class BehaviorManager implements ItemTypeProvider {
    public static final String NAME = "behavior";
    
    private static BehaviorManager instance;
    
    private DAO<Behavior> dao = null;
    
    @SuppressWarnings("unchecked")
    private BehaviorManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Behavior.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new BehaviorDBCleaner());
        
        ScheduleManager.getInstance().addJob(new PurgeBehavJob());
    }
    
    public static BehaviorManager getInstance() {
        if(instance == null) {
            instance = new BehaviorManager();
        }
        return instance;
    }
    
    public void createBehavior(Behavior behavior) {
        ItemManager.getInstance().checkDuplicate(behavior);
        
        this.dao.create(behavior);
    }
    
    public Behavior getBehavior(int id) {
        Behavior behavior = this.dao.get(id);
        if(behavior != null) {
            return behavior;
        }
        else {
            return null;
        }
    }
    
    public List<Behavior> getBehaviors() {
        List<Behavior> result = new ArrayList<Behavior>();
        for(Behavior behavior : this.dao.getAll()) {
            result.add(behavior);
        }
        return result;
    }
    
    public void updateBehavior(Behavior behavior) {
        this.dao.update(behavior);
    }
    
    public void deleteBehavior(int id) {
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
        return target instanceof Behavior;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Behavior behavior = (Behavior)target;
        return String.valueOf(behavior.id);
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
        
        Behavior behavior = (Behavior)target;
        return String.valueOf(behavior.id);
    }
    
    public String getLastBehaviorDisplay() {
        List<Behavior> behaviors = this.getBehaviors();
        if(behaviors.isEmpty()) {
            return "";
        }
        
        Collections.sort(behaviors, new Comparator<Behavior>(){

            @Override
            public int compare(Behavior o1, Behavior o2) {
                return -(o1.id - o2.id);
            }
            
        });
        
        Behavior last = behaviors.get(0);
        IBehaviorDef def = BehaviorDefManager.getInstance().getIBehaviorDef(last.defId);
        if(def == null) {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("Last track is [");
        sb.append(def.getName());
        sb.append("] ");
        sb.append(TimeUtils.getTimeReadableString(System.currentTimeMillis() - last.time));
        sb.append(" ago");
        
        return sb.toString();
    }
    
    public List<Behavior> getBehaviorsByDefId(int defId) {
        List<Behavior> ret = new ArrayList<Behavior>();
        
        for(Behavior behavior : this.getBehaviors()) {
            if(behavior.defId == defId) {
                ret.add(behavior);
            }
        }
        
        return ret;
    }
    
    public Map<String, Double> getBehaviorStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        int total = this.getBehaviors().size();
        if(total == 0) {
            return ret;
        }
        for(BehaviorDef def : BehaviorDefManager.getInstance().getBehaviorDefs()) {
            List<Behavior> behaviors = this.getBehaviorsByDefId(def.id);
            double ratio = FormatUtils.getRoundedValue(behaviors.size() * 100.0 / total);
            ret.put(def.name, ratio);
        }
        
        return ret;
    }
    
    public List<BehaviorFrequency> getBehaviorFrequencies() {
        List<BehaviorFrequency> ret = new ArrayList<BehaviorFrequency>();
        
        long now = System.currentTimeMillis();
        for(BehaviorDef def : BehaviorDefManager.getInstance().getBehaviorDefs()) {
            List<Behavior> behaviors = this.getBehaviorsByDefId(def.id);
            if(behaviors == null || behaviors.isEmpty()) {
                continue;
            }
            
            long earliest = 0;
            long latest = 0;
            for(Behavior behavior : behaviors) {
                if(earliest == 0 || behavior.time < earliest) {
                    earliest = behavior.time;
                }
                
                if(latest == 0 || behavior.time > latest) {
                    latest = behavior.time;
                }
            }
            
            long period = (now - earliest) / behaviors.size();
            BehaviorFrequency freq = new BehaviorFrequency();
            freq.name = def.name;
            freq.period = period;
            freq.lastStr = TimeUtils.getTimeReadableString(now - latest) + " ago";
            ret.add(freq);
        }
        
        return ret;
    }
}

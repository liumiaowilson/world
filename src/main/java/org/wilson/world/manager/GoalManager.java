package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.goal.GoalProgressMonitor;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Goal;
import org.wilson.world.model.GoalDef;

public class GoalManager implements ItemTypeProvider {
    public static final String NAME = "goal";
    
    private static GoalManager instance;
    
    private DAO<Goal> dao = null;
    
    @SuppressWarnings("unchecked")
    private GoalManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Goal.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        GoalProgressMonitor monitor = new GoalProgressMonitor();
        MonitorManager.getInstance().registerMonitorParticipant(monitor);
        EventManager.getInstance().registerListener(EventType.ReportGoal, monitor);
    }
    
    public static GoalManager getInstance() {
        if(instance == null) {
            instance = new GoalManager();
        }
        return instance;
    }
    
    public void createGoal(Goal goal) {
        ItemManager.getInstance().checkDuplicate(goal);
        
        this.dao.create(goal);
    }
    
    public Goal getGoal(int id) {
        Goal goal = this.dao.get(id);
        if(goal != null) {
            this.loadGoal(goal);
            return goal;
        }
        else {
            return null;
        }
    }
    
    private void loadGoal(Goal goal) {
        if(goal != null) {
            GoalDef def = GoalDefManager.getInstance().getGoalDef(goal.defId);
            if(def != null) {
                goal.name = def.name;
            }
        }
    }
    
    public List<Goal> getGoals() {
        List<Goal> result = new ArrayList<Goal>();
        for(Goal goal : this.dao.getAll()) {
            this.loadGoal(goal);
            result.add(goal);
        }
        return result;
    }
    
    public void updateGoal(Goal goal) {
        this.dao.update(goal);
    }
    
    public void deleteGoal(int id) {
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
        return target instanceof Goal;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Goal goal = (Goal)target;
        return String.valueOf(goal.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return null;
    }
    
    public Goal getGoalByDefId(int defId) {
        for(Goal goal : this.getGoals()) {
            if(goal.defId == defId) {
                return goal;
            }
        }
        
        return null;
    }
    
    public Goal reportGoal(int defId, long time, int amount) {
        Goal goal = this.getGoalByDefId(defId);
        if(goal == null) {
            goal = new Goal();
            goal.defId = defId;
            goal.time = time;
            goal.amount = amount;
            this.createGoal(goal);
        }
        else {
            goal.time = time;
            goal.amount = amount;
            this.updateGoal(goal);
        }
        
        return goal;
    }
    
    public String getDisplay(Goal goal) {
        if(goal == null) {
            return null;
        }
        
        GoalDef def = GoalDefManager.getInstance().getGoalDef(goal.defId);
        if(def == null) {
            return null;
        }
        
        double ratio = 1.0 * (def.endAmount - def.startAmount) / (def.endTime - def.startTime);
        long time = goal.time;
        if(time > def.endTime) {
            time = def.endTime;
        }
        int expected = (int) (def.startAmount + (time - def.startTime) * ratio);
        int delta = goal.amount - expected;
        
        int expected_pct = (expected - def.startAmount) * 100 / (def.endAmount - def.startAmount);
        int delta_pct = delta * 100 / (def.endAmount - def.startAmount);
        
        StringBuffer sb = new StringBuffer();
        sb.append("<div class='progress'><div class='progress-bar progress-bar-info' role='progressbar' style='width:");
        if((ratio > 0 && delta_pct > 0) || (ratio < 0 && delta_pct < 0)) {
            sb.append(expected_pct);
            sb.append("%'>Current</div><div class='progress-bar progress-bar-success' role='progressbar' style='width:");
            sb.append(Math.abs(delta_pct));
            sb.append("%'>Ahead</div>");
        }
        else if((ratio > 0 && delta_pct < 0) || (ratio < 0 && delta_pct > 0)) {
            sb.append(expected_pct + delta_pct);
            sb.append("%'>Current</div><div class='progress-bar progress-bar-danger' role='progressbar' style='width:");
            sb.append(Math.abs(delta_pct));
            sb.append("%'>Lagging</div>");
        }
        else {
            sb.append(expected_pct);
            sb.append("%'>Current</div>");
        }
        sb.append("</div>");
        
        return sb.toString();
    }
    
    public List<Goal> getAvailableGoals() {
        List<Goal> ret = new ArrayList<Goal>();
        
        long now = System.currentTimeMillis();
        for(GoalDef def : GoalDefManager.getInstance().getGoalDefs()) {
            if(now > def.startTime) {
                Goal goal = this.getGoalByDefId(def.id);
                if(goal == null) {
                    goal = new Goal();
                    goal.defId = def.id;
                    goal.name = def.name;
                    goal.time = now;
                    goal.amount = def.startAmount;
                    ret.add(goal);
                }
                else {
                    ret.add(goal);
                }
            }
        }
        
        return ret;
    }
    
    public Goal getNextGoal(GoalDef def) {
        if(def == null) {
            return null;
        }
        
        Goal goal = new Goal();
        goal.defId = def.id;
        
        if(def.steps == 0) {
            goal.amount = def.endAmount;
            goal.time = def.endTime;
        }
        else {
            long now = System.currentTimeMillis();
            
            double ratio = 1.0 * (def.endAmount - def.startAmount) / (def.endTime - def.startTime);
            
            long piece = (def.endTime - def.startTime) / def.steps;
            int p = 0;
            for(int i = 1; i <= def.steps; i++) {
                if(def.startTime + i * piece > now) {
                    p = i;
                    break;
                }
            }
            if(p == 0) {
                p = def.steps;
            }
            
            int delta = (int) (p * piece * ratio);
            goal.time = def.startTime + p * piece;
            goal.amount = def.startAmount + delta;
        }
        
        return goal;
    }
}

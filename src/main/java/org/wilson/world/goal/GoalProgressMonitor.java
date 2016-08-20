package org.wilson.world.goal;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.GoalDefManager;
import org.wilson.world.manager.GoalManager;
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Goal;
import org.wilson.world.model.GoalDef;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.util.TimeUtils;

public class GoalProgressMonitor implements MonitorParticipant, EventListener {
    private Alert alert = null;
    
    public GoalProgressMonitor() {
        this.alert = new Alert();
        this.alert.id = "Goal Progress Report";
        this.alert.message = "Please report goal progress as soon as possible.";
        this.alert.canAck = true;
    }
    
    @Override
    public boolean isOK() {
        long now = System.currentTimeMillis();
        
        for(GoalDef def : GoalDefManager.getInstance().getGoalDefs()) {
            if(def.startTime > now) {
                continue;
            }
            
            Goal goal = GoalManager.getInstance().getGoalByDefId(def.id);
            if(goal == null) {
                this.alert.url = "goal_report.jsp?id=" + def.id;
                return false;
            }
            else {
                int steps = def.steps;
                if(steps == 0) {
                    steps = 1;
                }
                long piece = (def.endTime - def.startTime) / steps;
                for(int i = 1; i < steps; i++) {
                    long point = def.startTime + piece * i;
                    if(point < now && point > goal.time + TimeUtils.DAY_DURATION) {
                        this.alert.url = "goal_report.jsp?id=" + def.id;
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.ReportGoal.equals(event.type)) {
            MonitorManager.getInstance().removeAlert(this.getAlert());
        }
    }

}

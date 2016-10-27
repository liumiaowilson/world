package org.wilson.world.habit;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.model.HabitTrace;

public class HabitCheckEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.CheckHabit.equals(event.type)) {
            HabitTrace trace = (HabitTrace) event.data.get("data");
            if(trace != null) {
                int n = trace.streak / 5;
                if(n > 3) {
                    n = 3;
                }
                else if(n == 0) {
                	if(DiceManager.getInstance().dice(50)) {
                		n = 1;
                	}
                }
                
                if(n > 0) {
                    int points = CharManager.getInstance().getSkillPoints();
                    points += n;
                    CharManager.getInstance().setSkillPoints(points);
                    
                    NotifyManager.getInstance().notifySuccess("Gained [" + n + "] skill point(s).");
                }
            }
        }
    }

}

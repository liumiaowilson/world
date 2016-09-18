package org.wilson.world.quest;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.QuestManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Quest;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.util.TimeUtils;

public class QuestMonitor implements MonitorParticipant {
    private Alert alert;
    
    public QuestMonitor() {
        this.alert = new Alert();
        this.alert.id = "Quests needed";
        this.alert.message = "No quests finished for a long time.";
        this.alert.canAck = true;
        this.alert.url = "quest_info.jsp";
    }
    
    @Override
    public boolean isOK() {
        int hours = ConfigManager.getInstance().getConfigAsInt("quest.max_idle.hours", 48);
        Quest quest = QuestManager.getInstance().getLastCreatedQuest();
        
        if(System.currentTimeMillis() - quest.time > hours * TimeUtils.HOUR_DURATION) {
            return false;
        }
        
        return true;
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}

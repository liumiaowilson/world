package org.wilson.world.quest;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.QuestDefManager;
import org.wilson.world.manager.QuestManager;
import org.wilson.world.model.Quest;
import org.wilson.world.model.QuestDef;
import org.wilson.world.model.QuestInfo;

public class QuestAchieveEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(event == null) {
            return;
        }
        
        Quest quest = (Quest) event.data.get("data");
        if(quest != null) {
            QuestDef def = QuestDefManager.getInstance().getQuestDef(quest.defId);
            if(def != null) {
                QuestInfo info = QuestManager.getInstance().getQuestInfo(def.name);
                if(info != null) {
                    int pay = info.pay;
                    int coins = CharManager.getInstance().getCoins();
                    coins = coins + pay;
                    CharManager.getInstance().setCoins(coins);
                    
                    NotifyManager.getInstance().notifySuccess("Gained [" + pay + "] coins. Now you have [" + coins + "] coins.");
                }
            }
        }
    }

}

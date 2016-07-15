package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.reward.ExpRewardGiver;
import org.wilson.world.reward.LifeRewardGiver;
import org.wilson.world.reward.RewardGiver;

public class RewardManager implements EventListener {
    private static final Logger logger = Logger.getLogger(RewardManager.class);
    
    private static RewardManager instance;
    
    private List<RewardGiver> givers = new ArrayList<RewardGiver>();
    
    private RewardManager() {
        this.load();
        this.loadFromConfig();
        
        EventManager.getInstance().registerListener(EventType.GainEvent, this);
    }
    
    private void load() {
        this.givers.add(new ExpRewardGiver());
        this.givers.add(new LifeRewardGiver());
    }
    
    private void loadFromConfig() {
        String value = ConfigManager.getInstance().getConfig("reward_giver.extensions");
        if(!StringUtils.isBlank(value)) {
            String [] items = value.split(",");
            for(String actionName : items) {
                actionName = actionName.trim();
                if(!StringUtils.isBlank(actionName)) {
                    RewardGiver rg = (RewardGiver) ExtManager.getInstance().wrapAction(actionName, RewardGiver.class);
                    if(rg != null) {
                        this.givers.add(rg);
                        logger.info("Load reward giver [" + actionName + "] from config.");
                    }
                }
            }
        }
    }
    
    public static RewardManager getInstance() {
        if(instance == null) {
            instance = new RewardManager();
        }
        return instance;
    }
    
    public void giveReward(Event event) {
        for(RewardGiver giver : givers) {
            giver.giveReward(event);
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        //get the real event causing the gaining exp event
        Event e = (Event) event.data.get("event");
        this.giveReward(e);
    }
}

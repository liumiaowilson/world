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
import org.wilson.world.reward.Reward;
import org.wilson.world.reward.RewardGiver;
import org.wilson.world.reward.RewardType;
import org.wilson.world.reward.ShowUpJob;

public class RewardManager implements EventListener {
    private static final Logger logger = Logger.getLogger(RewardManager.class);
    
    private static RewardManager instance;
    
    private List<RewardGiver> givers = new ArrayList<RewardGiver>();
    
    private Reward showUpReward = null;
    
    private RewardManager() {
        this.load();
        this.loadFromConfig();
        
        EventManager.getInstance().registerListener(EventType.GainExperience, this);
        
        ScheduleManager.getInstance().addJob(new ShowUpJob());
    }
    
    private void load() {
        this.givers.add(new ExpRewardGiver());
        this.givers.add(new LifeRewardGiver());
    }
    
    public void addRewardGiver(RewardGiver giver) {
        this.givers.add(giver);
    }
    
    private void loadFromConfig() {
        String value = ConfigManager.getInstance().getConfig("reward_giver.extensions");
        if(!StringUtils.isBlank(value)) {
            String [] items = value.split(",");
            for(String actionName : items) {
                actionName = actionName.trim();
                if(!StringUtils.isBlank(actionName)) {
                    RewardGiver rg = (RewardGiver) ExtManager.getInstance().getExtension(actionName, RewardGiver.class);
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
    
    public void setShowUpReward(Reward reward) {
    	this.showUpReward = reward;
    }
    
    public Reward getShowUpReward() {
    	return this.showUpReward;
    }
    
    public Reward generateReward(int max) {
    	int n = DiceManager.getInstance().random(max);
        if(n < 1) {
        	n = 1;
        }
    	
    	Reward reward = new Reward();
        reward.amount = n;
        RewardType [] types = RewardType.values();
        int p = DiceManager.getInstance().random(types.length);
        reward.type = types[p];
        
        return reward;
    }
    
    public String showUp() {
    	Reward reward = this.getShowUpReward();
    	if(reward == null) {
    		return "No reward could be delivered.";
    	}
    	
    	this.deliver(reward);
    	this.setShowUpReward(null);
    	
    	return null;
    }
    
    public void deliver(Reward reward) {
    	if(reward == null) {
    		return;
    	}
    	
    	switch(reward.type) {
    		case Exp:
    			this.deliverExps(reward.amount);
    			break;
    		case Coin:
    			this.deliverCoins(reward.amount);
    			break;
    		case SkillPoint:
    			this.deliverSkillPoints(reward.amount);
    			break;
    		default:
    			//do nothing
    	}
    }
    
    private void deliverExps(int amount) {
    	int exp = ExpManager.getInstance().getExp();
    	exp += amount;
    	ExpManager.getInstance().setExp(exp);
    	NotifyManager.getInstance().notifySuccess("Gained [" + amount + "] exp.");
    }
    
    private void deliverCoins(int amount) {
    	int coins = CharManager.getInstance().getCoins();
    	coins += amount;
    	CharManager.getInstance().setCoins(coins);
    	NotifyManager.getInstance().notifySuccess("Gained [" + amount + "] coins.");
    }
    
    private void deliverSkillPoints(int amount) {
    	int sps = CharManager.getInstance().getSkillPoints();
    	sps += amount;
    	CharManager.getInstance().setSkillPoints(sps);
    	NotifyManager.getInstance().notifySuccess("Gained [" + amount + "] skill points.");
    }
}

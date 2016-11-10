package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.behavior.IBehaviorDef;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.character.CharRecoverJob;
import org.wilson.world.character.CoinMissionRewardGenerator;
import org.wilson.world.character.DisasterJob;
import org.wilson.world.character.SkillPointMissionRewardGenerator;
import org.wilson.world.character.StatusRefreshJob;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Behavior;
import org.wilson.world.model.DataItem;
import org.wilson.world.model.StatusEffect;
import org.wilson.world.model.User;
import org.wilson.world.model.UserSkill;
import org.wilson.world.skill.Skill;
import org.wilson.world.skill.SkillStyle;
import org.wilson.world.status.IStatus;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.GameSkill;
import org.wilson.world.util.TimeUtils;

public class CharManager implements EventListener, ManagerLifecycle{
    private static final Logger logger = Logger.getLogger(CharManager.class);
    
    private static CharManager instance;
    
    private static final String USER_STATUS_PREFIX = "ustatus.";
    
    private Cache<String, StatusEffect> statusCache = null;
    
    private CharManager() {
        statusCache = new DefaultCache<String, StatusEffect>("char_manager_status_cache", false);
        
        ScheduleManager.getInstance().addJob(new DisasterJob());
        ScheduleManager.getInstance().addJob(new StatusRefreshJob());
        ScheduleManager.getInstance().addJob(new CharRecoverJob());
        
        EventManager.getInstance().registerListener(EventType.GainExperience, this);
        EventManager.getInstance().registerListener(EventType.Login, this);
        EventManager.getInstance().registerListener(EventType.CreateBehavior, this);
        
        MissionManager.getInstance().addMissionRewardGenerator(new CoinMissionRewardGenerator());
        MissionManager.getInstance().addMissionRewardGenerator(new SkillPointMissionRewardGenerator());
    }
    
    public static CharManager getInstance() {
        if(instance == null) {
            instance = new CharManager();
        }
        return instance;
    }
    
    public int getMaxHP() {
        return DataManager.getInstance().getValueAsInt("user.max_hp");
    }
    
    public void setMaxHP(int max_hp) {
        DataManager.getInstance().setValue("user.max_hp", max_hp);
    }
    
    public int getHP() {
        return DataManager.getInstance().getValueAsInt("user.hp");
    }
    
    public void setHP(int hp) {
        DataManager.getInstance().setValue("user.hp", hp);
    }
    
    public int getKills() {
        return DataManager.getInstance().getValueAsInt("user.kills");
    }
    
    public void setKills(int kills) {
        DataManager.getInstance().setValue("user.kills", kills);
    }
    
    public int getMaxMP() {
        return DataManager.getInstance().getValueAsInt("user.max_mp");
    }
    
    public void setMaxMP(int max_mp) {
        DataManager.getInstance().setValue("user.max_mp", max_mp);
    }
    
    public int getMP() {
        return DataManager.getInstance().getValueAsInt("user.mp");
    }
    
    public void setMP(int mp) {
        DataManager.getInstance().setValue("user.mp", mp);
    }
    
    public int getMaxStamina() {
        return DataManager.getInstance().getValueAsInt("user.max_stamina");
    }
    
    public void setMaxStamina(int max_stamina) {
        DataManager.getInstance().setValue("user.max_stamina", max_stamina);
    }
    
    public int getStamina() {
        return DataManager.getInstance().getValueAsInt("user.stamina");
    }
    
    public void setStamina(int stamina) {
        DataManager.getInstance().setValue("user.stamina", stamina);
    }
    
    public int getSpeed() {
        return DataManager.getInstance().getValueAsInt("user.speed");
    }
    
    public void setSpeed(int speed) {
        DataManager.getInstance().setValue("user.speed", speed);
    }
    
    public int getStrength() {
        return DataManager.getInstance().getValueAsInt("user.strength");
    }
    
    public void setStrength(int strength) {
        DataManager.getInstance().setValue("user.strength", strength);
    }
    
    public int getConstruction() {
        return DataManager.getInstance().getValueAsInt("user.construction");
    }
    
    public void setConstruction(int construction) {
        DataManager.getInstance().setValue("user.construction", construction);
    }
    
    public int getDexterity() {
        return DataManager.getInstance().getValueAsInt("user.dexterity");
    }
    
    public void setDexterity(int dexterity) {
        DataManager.getInstance().setValue("user.dexterity", dexterity);
    }
    
    public int getIntelligence() {
        return DataManager.getInstance().getValueAsInt("user.intelligence");
    }
    
    public void setIntelligence(int intelligence) {
        DataManager.getInstance().setValue("user.intelligence", intelligence);
    }
    
    public int getCharisma() {
        return DataManager.getInstance().getValueAsInt("user.charisma");
    }
    
    public void setCharisma(int charisma) {
        DataManager.getInstance().setValue("user.charisma", charisma);
    }
    
    public int getWillpower() {
        return DataManager.getInstance().getValueAsInt("user.willpower");
    }
    
    public void setWillpower(int willpower) {
        DataManager.getInstance().setValue("user.willpower", willpower);
    }
    
    public int getLuck() {
        return DataManager.getInstance().getValueAsInt("user.luck");
    }
    
    public void setLuck(int luck) {
        DataManager.getInstance().setValue("user.luck", luck);
    }
    
    public int getCoins() {
        return DataManager.getInstance().getValueAsInt("user.coins");
    }
    
    public int getKarma() {
        return DataManager.getInstance().getValueAsInt("user.karma");
    }
    
    public void setKarma(int karma) {
        int max = this.getMaxKarma();
        if(karma > max) {
            karma = max;
            //do auto karma redemption
            boolean enabled = ConfigManager.getInstance().getConfigAsBoolean("karma.redemption.auto.enabled", true);
            if(enabled) {
            	this.redeemKarma();
            }
        }
        else if(karma < -max) {
            karma = -max;
        }
        
        DataManager.getInstance().setValue("user.karma", karma);
    }
    
    public int getMaxKarma() {
        return DataManager.getInstance().getValueAsInt("user.max_karma");
    }
    
    public void setMaxKarma(int karma) {
        DataManager.getInstance().setValue("user.max_karma", karma);
    }
    
    public int getExchangeRatio() {
        return ConfigManager.getInstance().getConfigAsInt("coin.to.skillpoint.ratio", 5);
    }
    
    public int getMaxCoinsPossible() {
        int coins = this.getCoins();
        int points = this.getSkillPoints();
        return coins + points * this.getExchangeRatio();
    }
    
    public int getMaxSkillPointsPossible() {
        int coins = this.getCoins();
        int points = this.getSkillPoints();
        return points + coins/this.getExchangeRatio();
    }
    
    public void setCoins(int coins) {
        DataManager.getInstance().setValue("user.coins", coins);
    }
    
    public int getSkillPoints() {
        return DataManager.getInstance().getValueAsInt("user.skillpoints");
    }
    
    public void setSkillPoints(int points) {
        DataManager.getInstance().setValue("user.skillpoints", points);
    }
    
    public int increaseHP(int delta) {
        int old_hp = this.getHP();
        int max_hp = this.getMaxHP();
        int hp = old_hp + delta;
        if(hp > max_hp) {
            hp = max_hp;
        }
        int ret = hp - old_hp;
        if(ret != 0) {
            this.setHP(hp);
        }
        return ret;
    }
    
    public int getCurrentHPPercentage() {
        int max_hp = this.getMaxHP();
        int hp = this.getHP();
        int pct = (int) (hp * 100.0 / max_hp);
        return pct;
    }
    
    public int getCurrentMPPercentage() {
        int max_mp = this.getMaxMP();
        int mp = this.getMP();
        int pct = (int) (mp * 100.0 / max_mp);
        return pct;
    }
    
    public int getCurrentStaminaPercentage() {
        int max_stamina = this.getMaxStamina();
        int stamina = this.getStamina();
        int pct = (int) (stamina * 100.0 / max_stamina);
        return pct;
    }
    
    public void restore() {
        int hp = this.getMaxHP();
        this.setHP(hp);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.GainExperience == event.type) {
            boolean isProtected = DataManager.getInstance().getValueAsBoolean("user.protected");
            if(isProtected) {
                DataManager.getInstance().setValue("user.protected", false);
            }
        }
        else if(EventType.Login == event.type) {
            this.tryLuck();
        }
        else if(EventType.CreateBehavior == event.type) {
            Behavior behavior = (Behavior) event.data.get("data");
            if(behavior != null) {
                IBehaviorDef def = BehaviorDefManager.getInstance().getIBehaviorDef(behavior.defId);
                if(def != null) {
                    int karma = this.getKarma();
                    karma += def.getKarma();
                    this.setKarma(karma);
                }
            }
        }
    }
    
    public String getStatusRemainingTime(StatusEffect effect) {
        if(effect == null) {
            return null;
        }
        
        long validTo = effect.validTo;
        if(validTo < 0) {
            return null;
        }
        else {
            return TimeUtils.getRemainingTime(validTo);
        }
    }
    
    public void acquireStatus(IStatus status, int hours) {
        if(status == null) {
            return;
        }
        
        status.activate();
        
        long validTo = System.currentTimeMillis() + TimeUtils.HOUR_DURATION * hours;
        DataManager.getInstance().setValue(USER_STATUS_PREFIX + String.valueOf(status.getID()), validTo);
    }
    
    public void loseStatus(IStatus status) {
        if(status == null) {
            return;
        }
        DataManager.getInstance().deleteValue(USER_STATUS_PREFIX + String.valueOf(status.getID()));
        
        status.deactivate();
    }
    
    public List<StatusEffect> getStatusEffects() {
        return this.statusCache.getAll();
    }
    
    public List<StatusEffect> getValidStatusEffects() {
        List<StatusEffect> ret = new ArrayList<StatusEffect>();
        
        for(StatusEffect effect : this.getStatusEffects()) {
            if(this.isValidStatus(effect)) {
                ret.add(effect);
            }
        }
        
        return ret;
    }
    
    public boolean isValidStatus(StatusEffect effect) {
        if(effect == null || effect.status == null) {
            return false;
        }
        return this.isValidStatus(effect.status.getName());
    }
    
    public boolean isValidStatus(String statusName) {
        if(StringUtils.isBlank(statusName)) {
            return false;
        }
        StatusEffect old = this.statusCache.get(statusName);
        if(old != null) {
            return old.validTo > System.currentTimeMillis();
        }
        else {
            return false;
        }
    }
    
    public void tryLuck() {
        if(DiceManager.getInstance().dice(10)) {
            IStatus status = StatusManager.getInstance().randomStatus();
            this.acquireStatus(status, 24);
            
            NotifyManager.getInstance().notifySuccess("Luckily acquired a random status [" + status.getName() + "] for 24 hours.");
        }
    }
    
    public boolean hasStatus(String name) {
        if(StringUtils.isBlank(name)) {
            return false;
        }
        
        return this.isValidStatus(name);
    }

    @Override
    public void start() {
        DataManager.getInstance().addCacheListener(new CacheListener<DataItem>(){

            @Override
            public void cachePut(DataItem old, DataItem v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                if(v.name.startsWith(USER_STATUS_PREFIX)) {
                    String idStr = v.name.substring(USER_STATUS_PREFIX.length());
                    String validToStr = v.value;
                    try {
                        int id = Integer.parseInt(idStr);
                        long validTo = Long.parseLong(validToStr);
                        IStatus status = StatusManager.getInstance().getIStatus(id);
                        StatusEffect effect = new StatusEffect();
                        effect.status = status;
                        effect.validTo = validTo;
                        CharManager.this.statusCache.put(effect.status.getName(), effect);
                    }
                    catch(Exception e) {
                        logger.warn("Failed to parse user status data.");
                    }
                }
            }

            @Override
            public void cacheDeleted(DataItem v) {
                if(v.name.startsWith(USER_STATUS_PREFIX)) {
                    String idStr = v.name.substring(USER_STATUS_PREFIX.length());
                    try {
                        int id = Integer.parseInt(idStr);
                        IStatus status = StatusManager.getInstance().getIStatus(id);
                        CharManager.this.statusCache.delete(status.getName());
                    }
                    catch(Exception e) {
                        logger.warn("Failed to parse user status data.");
                    }
                }
            }

            @Override
            public void cacheLoaded(List<DataItem> all) {
                //specially for char manager here
                for(DataItem item : all) {
                    cachePut(null, item);
                }
            }

            @Override
            public void cacheLoading(List<DataItem> old) {
                CharManager.this.statusCache.clear();
            }
            
        });
        
        DataManager.getInstance().notifyLoaded();
    }

    @Override
    public void shutdown() {
    }
    
    public Attacker getAttacker() {
        String name = "Unknown";
        User user = UserManager.getInstance().getCurrentUser();
        if(user != null) {
            name = user.username;
        }
        Attacker ret = new Attacker(name, SkillStyle.Balanced);
        ret.setSpeed(this.getSpeed());
        
        ret.setMaxHp(this.getMaxHP());
        ret.setHp(this.getHP());
        ret.setMaxMp(this.getMaxMP());
        ret.setMp(this.getMP());
        
        ret.setStrength(this.getStrength());
        ret.setConstruction(this.getConstruction());
        ret.setDexterity(this.getDexterity());
        ret.setIntelligence(this.getIntelligence());
        ret.setCharisma(this.getCharisma());
        ret.setWillpower(this.getWillpower());
        ret.setLuck(this.getLuck());
        
        List<UserSkill> usList = UserSkillManager.getInstance().getUserSkills();
        for(UserSkill us : usList) {
            Skill skill = SkillDataManager.getInstance().getSkill(us.skillId);
            GameSkill gs = new GameSkill(skill, us.level, us.exp);
            ret.getSkills().add(gs);
        }
        
        return ret;
    }
    
    public void setAttacker(Attacker attacker) {
        if(attacker == null) {
            return;
        }
        this.setHP(attacker.getHp());
        this.setMP(attacker.getMp());
    }
    
    public int getAttributeLimit() {
        return ExpManager.getInstance().getLevel() * 2;
    }
    
    public int getMaxHPLimit() {
        return ExpManager.getInstance().getLevel() * 20;
    }
    
    public int getMaxMPLimit() {
        return ExpManager.getInstance().getLevel() * 20;
    }
    
    public int getMaxStaminaLimit() {
        return ExpManager.getInstance().getLevel() * 20;
    }
    
    public boolean canRedeemKarma() {
    	int maxKarma = this.getMaxKarma();
    	int karma = this.getKarma();
    	return karma >= maxKarma / 2;
    }
    
    public String redeemKarma() {
    	if(!this.canRedeemKarma()) {
    		return "Karma cannot be redeemed";
    	}
    	
    	int maxKarma = this.getMaxKarma();
    	int karma = this.getKarma();
    	karma = karma - maxKarma / 2;
    	this.setKarma(karma);
    	
    	int points = this.getSkillPoints();
    	points += 1;
    	this.setSkillPoints(points);
    	
    	return null;
    }
}

package org.wilson.world.tick;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.SkillDataManager;
import org.wilson.world.skill.Skill;
import org.wilson.world.skill.SkillStyle;

public class Attacker extends Actor {
    private int id;
    private int maxHp = 100;
    private int hp = 100;
    private int maxMp = 100;
    private int mp = 100;
    
    private int strength = 10;
    private int construction = 10;
    private int dexterity = 10;
    private int intelligence = 10;
    private int charisma = 10;
    private int willpower = 10;
    private int luck = 10;
    
    private SkillStyle style = null;
    
    private List<GameSkill> skills = new ArrayList<GameSkill>();
    
    public Attacker(String name, SkillStyle style) {
        this.setName(name);
        this.setSpeed(50);
        this.setStyle(style);
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public SkillStyle getStyle() {
        return style;
    }

    public void setStyle(SkillStyle style) {
        this.style = style;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getStrength() {
        return strength;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getConstruction() {
        return construction;
    }

    public void setConstruction(int construction) {
        this.construction = construction;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getWillpower() {
        return willpower;
    }

    public void setWillpower(int willpower) {
        this.willpower = willpower;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
    
    public List<GameSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<GameSkill> skills) {
        this.skills = skills;
    }

    public Attacker getTargetAttacker() {
        Actor actor = this.getTarget();
        if(actor instanceof Attacker) {
            return (Attacker)actor;
        }
        else {
            return null;
        }
    }
    
    public int getDamage() {
        return DiceManager.getInstance().roll(this.strength, 0.5, 1.5);
    }
    
    protected boolean canHit(Attacker target) {
        return DiceManager.getInstance().dice(this.getDexterity(), (int)(target.getDexterity() * 0.6)) > 0;
    }
    
    protected int getDefense(Attacker target) {
        int delta = DiceManager.getInstance().dice(this.getStrength(), target.getConstruction());
        if(delta >= 0) {
            return 0;
        }
        else {
            return -delta;
        }
    }
    
    protected int causeDamage(Attacker target, int damage) {
        int def = this.getDefense(target);
        int defended = (int) (damage * 1.0 * def / target.getConstruction());
        damage = damage - defended;
        if(damage <= 0) {
            damage = 1;
        }
        int old_hp = target.getHp();
        int hp = old_hp - damage;
        target.setHp(hp);
        return damage;
    }
    
    protected List<GameSkill> findAvailableSkills(GameSkillType type, int stepId) {
        List<GameSkill> skills = Attacker.findSkills(this.skills, type);
        List<GameSkill> ret = new ArrayList<GameSkill>();
        
        for(GameSkill skill : skills) {
            if(skill.getStepId() + skill.getCooldown() > stepId) {
                if(this.getMp() >= skill.getCost()) {
                    ret.add(skill);
                }
            }
        }
        
        return ret;
    }
    
    public void doAttack(Attacker target, int stepId, TickMonitor monitor) {
        if(tryEscape(target, stepId, monitor)) {
            return;
        }
        
        if(tryRecoverHP(target, stepId, monitor)) {
            return;
        }
        
        if(tryRecoverMP(target, stepId, monitor)) {
            return;
        }
        
        if(tryBuf(target, stepId, monitor)) {
            return;
        }
        
        if(tryDebuf(target, stepId, monitor)) {
            return;
        }
        
        if(tryAttack(target, stepId, monitor)) {
            return;
        }
        
        boolean canHit = this.canHit(target);
        if(canHit) {
            int damage = this.getDamage();
            int d = this.causeDamage(target, damage);
            monitor.send(message(target, "Caused [" + d + "] out of [" + damage + "] physical damage."));
        }
        else {
            monitor.send(message(target, "Failed to hit " + target.toString() + "."));
        }
    }
    
    protected boolean tryAttack(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_attack)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.Attack, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to attack."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    protected boolean tryDebuf(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_debuf)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.Debuf, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to set debuf."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    protected boolean tryBuf(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_buf)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.Buf, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to set buf."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    protected boolean tryRecoverMP(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_recover_mp)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.RecoverMP, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to recover MP."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    protected boolean tryRecoverHP(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_recover_hp)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.RecoverHP, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to recover HP."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    protected boolean tryEscape(Attacker target, int stepId, TickMonitor monitor) {
        if(DiceManager.getInstance().dice(this.style.p_escape)) {
            List<GameSkill> skills = this.findAvailableSkills(GameSkillType.Escape, stepId);
            boolean tried = false;
            for(GameSkill skill : skills) {
                if(skill.canTrigger(this, target, monitor)) {
                    skill.trigger(this, target, monitor);
                    skill.setStepId(stepId);
                    monitor.send(message(target, "Used skill[" + skill.getName() + "] to try to escape."));
                    tried = true;
                    break;
                }
            }
            return tried;
        }
        
        return false;
    }
    
    @Override
    public void doTick(int stepId, TickMonitor monitor) {
        //check health
        if(this.hp < 0) {
            monitor.send(message("I am dead."));
            monitor.setEnded(true);
            return;
        }
        
        //find target
        Attacker target = this.getTargetAttacker();
        if(target == null) {
            monitor.send(message("No target found."));
            monitor.setEnded(true);
            return;
        }
        
        //do attack
        doAttack(target, stepId, monitor);
    }

    @Override
    public Object getInfo() {
        AttackerInfo info = new AttackerInfo();
        info.name = this.getName();
        info.speed = this.getSpeed();
        
        info.maxHp = this.getMaxHp();
        info.hp = this.getHp();
        info.maxMp = this.getMaxMp();
        info.mp = this.getMp();
        
        info.strength = this.getStrength();
        info.construction = this.getConstruction();
        info.dexterity = this.getDexterity();
        info.intelligence = this.getIntelligence();
        info.charisma = this.getCharisma();
        info.willpower = this.getWillpower();
        info.luck = this.getLuck();
        return info;
    }
    
    public static Attacker clone(Attacker attacker) {
        Attacker ret = new Attacker(attacker.getName(), attacker.getStyle());
        ret.setSpeed(attacker.getSpeed());
        
        ret.setMaxHp(attacker.getMaxHp());
        ret.setHp(attacker.getHp());
        ret.setMaxMp(attacker.getMaxMp());
        ret.setMp(attacker.getMp());
        
        ret.setStrength(attacker.getStrength());
        ret.setConstruction(attacker.getConstruction());
        ret.setDexterity(attacker.getDexterity());
        ret.setIntelligence(attacker.getIntelligence());
        ret.setCharisma(attacker.getCharisma());
        ret.setWillpower(attacker.getWillpower());
        ret.setLuck(attacker.getLuck());
        
        return ret;
    }
    
    public static Attacker randomAttacker(Attacker base, String name) {
        if(base == null) {
            return null;
        }
        
        Attacker attacker = new Attacker(name, SkillStyle.random());
        attacker.setSpeed(DiceManager.getInstance().roll(base.getSpeed(), 0.5, 1.5));
        
        attacker.setMaxHp(DiceManager.getInstance().roll(base.getMaxHp(), 0.5, 1.5));
        attacker.setHp(attacker.getMaxHp());
        attacker.setMaxMp(DiceManager.getInstance().roll(base.getMaxMp(), 0.5, 1.5));
        attacker.setMp(attacker.getMaxMp());
        
        int avg = (base.getStrength() + base.getConstruction() + base.getDexterity() + base.getIntelligence() + base.getCharisma() + base.getWillpower() + base.getLuck()) / 7;
        attacker.setStrength(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setConstruction(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setDexterity(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setIntelligence(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setCharisma(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setWillpower(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        attacker.setLuck(DiceManager.getInstance().roll(avg, 0.5, 1.5));
        
        int num = getMaxNumOfSkills(attacker);
        num = DiceManager.getInstance().random(num);
        List<Skill> skills = SkillDataManager.getInstance().getSkills();
        List<Skill> ranSkills = DiceManager.getInstance().random(skills, num);
        for(Skill skill : ranSkills) {
            GameSkill gs = new GameSkill(skill, 1, 0);
            attacker.getSkills().add(gs);
        }
        
        return attacker;
    }
    
    public static int getMaxNumOfSkills(Attacker attacker) {
        if(attacker == null) {
            return 0;
        }
        return attacker.charisma / 5;
    }
    
    public static int compare(Attacker a1, Attacker a2) {
        if(a1 == null || a2 == null) {
            return 0;
        }
        
        int ret = 0;
        int [] array = new int [10];
        array[0] = a1.getHp() - a2.getHp();
        array[1] = a1.getMp() - a2.getMp();
        array[2] = a1.getSpeed() - a2.getSpeed();
        array[3] = a1.getStrength() - a2.getStrength();
        array[4] = a1.getConstruction() - a2.getConstruction();
        array[5] = a1.getDexterity() - a2.getDexterity();
        array[6] = a1.getIntelligence() - a2.getIntelligence();
        array[7] = a1.getCharisma() - a2.getCharisma();
        array[8] = a1.getWillpower() - a2.getWillpower();
        array[9] = a1.getLuck() - a2.getLuck();
        
        for(int i = 0; i < array.length; i++) {
            if(array[i] > 0) {
                ret += 1;
            }
        }
        
        return ret;
    }
    
    public static Attacker find(List<Attacker> attackers, String name) {
        if(attackers == null || attackers.isEmpty() || StringUtils.isBlank(name)) {
            return null;
        }
        for(Attacker attacker : attackers) {
            if(name.equals(attacker.getName())) {
                return attacker;
            }
        }
        return null;
    }
    
    public static Attacker findTarget(List<Attacker> attackers, String name) {
        if(attackers == null || attackers.isEmpty() || StringUtils.isBlank(name)) {
            return null;
        }
        for(Attacker attacker : attackers) {
            if(!name.equals(attacker.getName())) {
                return attacker;
            }
        }
        return null;
    }
    
    public static List<GameSkill> findSkills(List<GameSkill> skills, GameSkillType type) {
        List<GameSkill> ret = new ArrayList<GameSkill>();
        
        for(GameSkill skill : skills) {
            if(type.equals(skill.getType())) {
                ret.add(skill);
            }
        }
        
        return ret;
    }
}

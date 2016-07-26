package org.wilson.world.tick;

import org.wilson.world.manager.DiceManager;

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
    
    public Attacker(String name) {
        this.setName(name);
        this.setSpeed(50);
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
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
    
    public void doAttack(Attacker target, TickMonitor monitor) {
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
    
    @Override
    public void doTick(TickMonitor monitor) {
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
        doAttack(target, monitor);
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
    
    public static Attacker randomAttacker(Attacker base, String name) {
        if(base == null) {
            return null;
        }
        
        Attacker attacker = new Attacker(name);
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
        
        return attacker;
    }
}

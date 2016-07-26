package org.wilson.world.tick;

import org.wilson.world.manager.DiceManager;

public class Attacker extends Actor {
    private int maxHp;
    private int hp;
    private int strenth;
    
    public Attacker(String name) {
        this.setName(name);
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

    public int getStrenth() {
        return strenth;
    }

    public void setStrenth(int strenth) {
        this.strenth = strenth;
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
        return DiceManager.getInstance().roll(this.strenth, 0.5, 1.2);
    }
    
    public void causeDamage(Attacker target, int damage, TickMonitor monitor) {
        int old_hp = target.getHp();
        int hp = old_hp - damage;
        target.setHp(hp);
        monitor.send(message(target, "Caused [" + damage + "] damage."));
    }
    
    public void doAttack(Attacker target, TickMonitor monitor) {
        int damage = this.getDamage();
        this.causeDamage(target, damage, monitor);
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
        info.strength = this.getStrenth();
        return info;
    }
}

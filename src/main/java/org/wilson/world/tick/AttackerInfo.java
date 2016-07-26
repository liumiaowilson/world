package org.wilson.world.tick;

public class AttackerInfo {
    public String name;
    
    public int speed;
    
    public int maxHp;
    
    public int hp;
    
    public int strength;
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(name + " satus: ");
        sb.append("speed->" + speed + ",");
        sb.append("maxHp->" + maxHp + ",");
        sb.append("hp->" + hp + ",");
        sb.append("strength->" + strength + ",");
        
        return sb.toString();
    }
}

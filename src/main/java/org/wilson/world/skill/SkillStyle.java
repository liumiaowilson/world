package org.wilson.world.skill;

import org.wilson.world.manager.DiceManager;

public enum SkillStyle {
    Aggressive(25, 50, 50, 25, 75, 75),
    Balanced(50, 50, 50, 50, 50, 50),
    Defensive(50, 75, 50, 75, 25, 25),
    Conservative(75, 75, 50, 50, 25, 25);
    
    public int p_escape;
    public int p_recover_hp;
    public int p_recover_mp;
    public int p_buf;
    public int p_debuf;
    public int p_attack;
    
    SkillStyle(int p_escape, int p_recover_hp, int p_recover_mp, int p_buf, int p_debuf, int p_attack) {
        this.p_escape = p_escape;
        this.p_recover_hp = p_recover_hp;
        this.p_recover_mp = p_recover_mp;
        this.p_buf = p_buf;
        this.p_debuf = p_debuf;
        this.p_attack = p_attack;
    }
    
    public static SkillStyle random() {
        SkillStyle [] array = SkillStyle.values();
        int n = DiceManager.getInstance().random(array.length);
        return array[n];
    }
}

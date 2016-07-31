package org.wilson.world.manager;

import org.junit.Test;
import org.wilson.world.skill.SkillStyle;
import org.wilson.world.tick.Attacker;

public class TickManagerTest {

    @Test
    public void testBasic() {
        Attacker a1 = new Attacker("Wilson", SkillStyle.Balanced);
        a1.setMaxHp(100);
        a1.setHp(100);
        a1.setStrength(20);
        a1.setSpeed(20);
        
        Attacker a2 = new Attacker("Coco", SkillStyle.Balanced);
        a2.setMaxHp(100);
        a2.setHp(100);
        a2.setStrength(20);
        a2.setSpeed(20);
        
        TickManager tm = TickManager.getInstance();
        tm.addTickable(a1);
        tm.addTickable(a2);
        tm.addOutput(System.out);
        
        tm.play();
    }

}

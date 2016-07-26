package org.wilson.world.manager;

import org.junit.Test;
import org.wilson.world.tick.Attacker;

public class TickManagerTest {

    @Test
    public void testBasic() {
        Attacker a1 = new Attacker("Wilson");
        a1.setMaxHp(100);
        a1.setHp(100);
        a1.setStrenth(20);
        a1.setSpeed(20);
        
        Attacker a2 = new Attacker("Coco");
        a2.setMaxHp(100);
        a2.setHp(100);
        a2.setStrenth(20);
        a2.setSpeed(20);
        
        TickManager tm = TickManager.getInstance();
        tm.addTickable(a1);
        tm.addTickable(a2);
        tm.addOutput(System.out);
        
        tm.play();
    }

}

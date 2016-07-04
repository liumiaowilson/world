package org.wilson.world.manager;

import org.junit.Test;

public class ConsoleManagerTest {

    @Test
    public void testRun() {
        String result = ConsoleManager.getInstance().run("ls");
        System.out.println(result);
    }

}

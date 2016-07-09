package org.wilson.world.manager;

import org.junit.Test;

public class ScriptManagerTest {

    @Test
    public void testScript() {
        Object ret = ScriptManager.getInstance().run("1 + 1");
        System.out.println(ret);
    }

    @Test
    public void testManagers() {
        new ManagerLoader().contextInitialized(null);
        Object ret = ScriptManager.getInstance().run("dataManager");
        System.out.println(ret);
    }
}

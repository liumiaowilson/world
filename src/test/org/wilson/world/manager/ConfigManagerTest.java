package org.wilson.world.manager;

import org.apache.log4j.Level;
import org.junit.Test;

public class ConfigManagerTest {

    @Test
    public void testLoadConfig() {
        ConfigManager.getInstance();
    }

    @Test
    public void testSetLogLevel() {
        Level l = Level.toLevel("DEBUG");
        System.out.println(l);
    }
    
    @Test
    public void testIsPreload() {
        System.out.println(ConfigManager.getInstance().isPreloadOnStartup());
    }
}

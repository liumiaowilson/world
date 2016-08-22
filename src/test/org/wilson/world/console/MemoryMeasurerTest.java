package org.wilson.world.console;

import org.junit.Test;
import org.wilson.world.console.ObjectGraphMeasurer.Footprint;
import org.wilson.world.manager.ConfigManager;

public class MemoryMeasurerTest {

    @Test
    public void test() {
        Footprint ret = ObjectGraphMeasurer.measure(ConfigManager.getInstance());
        System.out.println(ret);
    }

}

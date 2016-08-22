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

    @Test
    public void testCycle() {
        Item item1 = new Item();
        Item item2 = new Item();
        item1.ref = item2;
        item2.ref = item1;
        Footprint ret = ObjectGraphMeasurer.measure(item1);
        System.out.println(ret);
    }
    
    @Test
    public void testPrimitive() {
        Footprint ret = ObjectGraphMeasurer.measure(1);
        System.out.println(ret);
    }
    
    public static class Item {
        public String name;
        
        public Item ref;
    }
}

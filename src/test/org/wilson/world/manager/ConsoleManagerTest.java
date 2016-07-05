package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ConsoleManagerTest {

    @Test
    public void testRun() {
        String result = ConsoleManager.getInstance().run("ls");
        System.out.println(result);
    }

    @Test
    public void testEnvironment() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for(String key : keys) {
            System.out.println(key + " -> " + env.get(key));
        }
    }
}

package org.wilson.world.manager;

import java.util.TimeZone;

import org.junit.Test;

public class StatsManagerTest {

    @Test
    public void testLong() {
        System.out.println(String.valueOf(Long.MAX_VALUE).length());
    }

    @Test
    public void testTime() {
        long current = System.currentTimeMillis();
        long last = current - 30 * 24 * 60 * 60 * 1000L;
        System.out.println(current);
        System.out.println(last);
        System.out.println(30 * 24 * 60 * 60 * 1000L);
    }
    
    @Test
    public void testTimezone() {
        for(String tz : TimeZone.getAvailableIDs()) {
            System.out.println(tz);
        }
    }
}

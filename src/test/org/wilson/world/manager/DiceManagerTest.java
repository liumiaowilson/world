package org.wilson.world.manager;

import java.util.Random;

import org.junit.Test;

public class DiceManagerTest {

    @Test
    public void testGaussian() {
        Random r = new Random();
        for(int i = 0; i < 50; i++) {
            System.out.println(r.nextGaussian());
        }
    }

}

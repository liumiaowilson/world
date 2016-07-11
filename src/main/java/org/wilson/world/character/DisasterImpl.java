package org.wilson.world.character;

import java.util.Random;

public class DisasterImpl implements Disaster {

    @Override
    public int getDamage(int level, int max_hp, int hp) {
        Random r = new Random();
        return 1 + r.nextInt(max_hp / 10);
    }

}

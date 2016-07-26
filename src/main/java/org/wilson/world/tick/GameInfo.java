package org.wilson.world.tick;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    public int steps;
    
    public List<Attacker> before = new ArrayList<Attacker>();
    
    public List<Attacker> after = new ArrayList<Attacker>();
}

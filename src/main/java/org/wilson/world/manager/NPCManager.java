package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.tick.Attacker;
import org.wilson.world.util.NameGenerator;

public class NPCManager implements ManagerLifecycle{
    private static final Logger logger = Logger.getLogger(NPCManager.class);
    
    private static NPCManager instance;
    
    private Map<Integer, Attacker> npcs = new HashMap<Integer, Attacker>();
    private static int GLOBAL_ID = 1;
    private NameGenerator nameGen = null;
    
    private NPCManager() {
        this.nameGen = new NameGenerator();
    }
    
    public static NPCManager getInstance() {
        if(instance == null) {
            instance = new NPCManager();
        }
        return instance;
    }
    
    private void loadNPCs() {
        Attacker base = CharManager.getInstance().getAttacker();
        for(int i = 0; i < 20; i++) {
            Attacker npc = Attacker.randomAttacker(base, this.nameGen.getName());
            npc.setId(GLOBAL_ID++);
            this.npcs.put(npc.getId(), npc);
        }
    }

    @Override
    public void start() {
        logger.info("Start to load NPC list");
        this.loadNPCs();
    }

    @Override
    public void shutdown() {
    }
    
    public Attacker getNPC(int id) {
        return this.npcs.get(id);
    }
    
    public List<Attacker> getNPCs() {
        return new ArrayList<Attacker>(this.npcs.values());
    }
}

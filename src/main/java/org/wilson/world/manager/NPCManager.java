package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.GameInfo;
import org.wilson.world.tick.RateInfo;
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
    
    public RateInfo rate(Attacker npc) {
        return rate(npc, 10);
    }
    
    public RateInfo rate(Attacker npc, int n) {
        if(npc == null) {
            return null;
        }
        
        RateInfo info = new RateInfo();
        List<GameInfo> games = new ArrayList<GameInfo>();
        
        Attacker user = CharManager.getInstance().getAttacker();
        for(int i = 0; i < n; i++) {
            TickManager.getInstance().reset();
            TickManager.getInstance().addTickable(Attacker.clone(user));
            TickManager.getInstance().addTickable(Attacker.clone(npc));
            games.add(TickManager.getInstance().play());
        }
        
        int sum_steps = 0;
        int sum_wins = 0;
        int sum_lost_hp = 0;
        for(GameInfo game : games) {
            sum_steps += game.steps;
            
            Attacker before = Attacker.find(game.before, user.getName());
            
            Attacker after = Attacker.find(game.after, user.getName());
            if(after.getHp() >= 0) {
                sum_wins++;
            }
            
            sum_lost_hp += before.getHp() - after.getHp();
        }
        info.avg_steps = sum_steps / n;
        info.win_rate = sum_wins * 100 / n;
        info.avg_lost_hp = sum_lost_hp / n;
        
        return info;
    }
}

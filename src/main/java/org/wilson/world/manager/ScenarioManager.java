package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Scenario;

public class ScenarioManager implements ItemTypeProvider {
    public static final String NAME = "scenario";
    
    private static ScenarioManager instance;
    
    private DAO<Scenario> dao = null;
    
    private Random r = null;
    
    private String reviveMessage = null;
    private String reactMessage = null;
    
    @SuppressWarnings("unchecked")
    private ScenarioManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Scenario.class);
        this.r = new Random();
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ScenarioManager getInstance() {
        if(instance == null) {
            instance = new ScenarioManager();
        }
        return instance;
    }
    
    public void createScenario(Scenario scenario) {
        this.dao.create(scenario);
    }
    
    public Scenario getScenario(int id) {
        Scenario scenario = this.dao.get(id);
        if(scenario != null) {
            return scenario;
        }
        else {
            return null;
        }
    }
    
    public List<Scenario> getScenarios() {
        List<Scenario> result = new ArrayList<Scenario>();
        for(Scenario scenario : this.dao.getAll()) {
            result.add(scenario);
        }
        return result;
    }
    
    public void updateScenario(Scenario scenario) {
        this.dao.update(scenario);
    }
    
    public void deleteScenario(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Scenario;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Scenario scenario = (Scenario)target;
        return String.valueOf(scenario.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public Scenario randomScenario() {
        List<Scenario> scenarios = this.getScenarios();
        if(scenarios.isEmpty()) {
            return null;
        }
        int idx = this.r.nextInt(scenarios.size());
        return scenarios.get(idx);
    }
    
    public void read(int id) {
        this.reviveMessage = null;
        this.reactMessage = null;
    }
    
    public void revive(int id, String description) {
        this.reviveMessage = description;
    }
    
    public void react(int id, String description) {
        this.reactMessage = description;
    }
    
    public void recap(int id, String description) {
        Scenario scenario = this.getScenario(id);
        if(scenario != null) {
            scenario.reaction = description;
            this.updateScenario(scenario);
        }
        
        this.reviveMessage = null;
        this.reactMessage = null;
    }
    
    public String getReviveMessage() {
        return this.reviveMessage;
    }
    
    public String getReactMessage() {
        return this.reactMessage;
    }
}

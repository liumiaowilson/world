package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Scenario;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

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
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Scenario scenario : getScenarios()) {
                    boolean found = scenario.name.contains(text) || scenario.stimuli.contains(text) || scenario.reaction.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = scenario.id;
                        content.name = scenario.name;
                        content.description = scenario.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ScenarioManager getInstance() {
        if(instance == null) {
            instance = new ScenarioManager();
        }
        return instance;
    }
    
    public void createScenario(Scenario scenario) {
        ItemManager.getInstance().checkDuplicate(scenario);
        
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Scenario scenario = (Scenario)target;
        return scenario.name;
    }
}

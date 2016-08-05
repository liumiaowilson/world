package org.wilson.world.festival;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class FestivalEngineFactory {
    private static FestivalEngineFactory instance;
    
    private Map<String, FestivalEngine> engines = new HashMap<String, FestivalEngine>();
    
    private FestivalEngineFactory() {
        this.loadEngines();
    }
    
    public static FestivalEngineFactory getInstance() {
        if(instance == null) {
            instance = new FestivalEngineFactory();
        }
        return instance;
    }
    
    private void loadEngines() {
        this.loadEngine(new SolarFestivalEngine());
        this.loadEngine(new LunarFestivalEngine());
        this.loadEngine(new WeekFestivalEngine());
        this.loadEngine(new EasterFestivalEngine());
    }
    
    private void loadEngine(FestivalEngine engine) {
        if(engine != null) {
            this.engines.put(engine.getName(), engine);
        }
    }
    
    public FestivalEngine getEngineByName(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        return this.engines.get(name);
    }
}

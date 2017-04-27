package org.wilson.world.festival;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.wilson.world.java.JavaExtensionListener;

public class FestivalEngineFactory implements JavaExtensionListener<FestivalEngine> {
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
        this.loadEngine(new MonthFestivalEngine());
    }
    
    private void loadEngine(FestivalEngine engine) {
        if(engine != null && engine.getName() != null) {
            this.engines.put(engine.getName(), engine);
        }
    }
    
    public FestivalEngine getEngineByName(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        return this.engines.get(name);
    }

    @Override
    public Class<FestivalEngine> getExtensionClass() {
        return FestivalEngine.class;
    }

    @Override
    public void created(FestivalEngine t) {
        this.loadEngine(t);
    }

    @Override
    public void removed(FestivalEngine t) {
        if(t != null && t.getName() != null) {
            this.engines.remove(t.getName());
        }
    }
}

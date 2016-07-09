package org.wilson.world.manager;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;

public class ScriptManager {
    private static final Logger logger = Logger.getLogger(ScriptManager.class);
    
    private static ScriptManager instance;
    
    private ScriptEngine engine;
    
    private ScriptManager() {
    }
    
    public static ScriptManager getInstance() {
        if(instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }
    
    private ScriptEngine getEngine() {
        if(this.engine == null) {
            ScriptEngineManager sem = new ScriptEngineManager();
            this.engine = sem.getEngineByName("JavaScript");
            List<Object> managers = ManagerLoader.getManagers();
            for(Object manager : managers) {
                String name = manager.getClass().getSimpleName();
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                this.engine.put(name, manager);
            }
        }
        return this.engine;
    }
    
    public Object run(String script) {
        if(StringUtils.isBlank(script)) {
            return null;
        }
        
        Object ret = null;
        try {
            ret = this.getEngine().eval(script);
        }
        catch(Exception e) {
            logger.error("failed to run script", e);
            throw new DataException(e.getMessage());
        }
        return ret;
    }
}

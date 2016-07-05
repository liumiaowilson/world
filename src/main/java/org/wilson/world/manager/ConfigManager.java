package org.wilson.world.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigManager {
    private static final Logger logger = Logger.getLogger(ConfigManager.class);
    
    private static ConfigManager instance;
    
    private Properties props = null;
    
    private ConfigManager() {
        try {
            props = new Properties();
            String url = null;
            if(isOpenShiftApp()) {
                url = "/world.openshift.properties";
            }
            else {
                url = "/world.dev.properties";
            }
            InputStream in = getClass().getResourceAsStream(url);
            props.load(in);
            in.close();
        }
        catch(Exception e) {
            logger.error("failed to load config file", e);
        }
    }
    
    public static ConfigManager getInstance() {
        if(instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public boolean isOpenShiftApp() {
        String name = System.getenv("OPENSHIFT_GEAR_NAME");
        return name != null;
    }
    
    public List<String> getConfigNames() {
        List<String> names = new ArrayList<String>();
        for(String n : this.props.stringPropertyNames()) {
            names.add(n);
        }
        return names;
    }
    
    public String getConfig(String name) {
        return this.props.getProperty(name);
    }
    
    public boolean getConfigAsBoolean(String name) {
        String value = this.getConfig(name);
        if("true".equalsIgnoreCase(value)) {
            return true;
        }
        else {
            return false;
        }
    }
}

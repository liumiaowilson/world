package org.wilson.world.manager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ManagerLoader implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ManagerLoader.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("Manager loader context destroyed.");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("Manager loader context initialized.");
        
        EventManager.getInstance();
        ConfigManager.getInstance();
        ConsoleManager.getInstance();
        SecManager.getInstance();
        
        ItemManager.getInstance();
        CacheManager.getInstance();
        MarkManager.getInstance();
        UserManager.getInstance();
        IdeaManager.getInstance();
        DataManager.getInstance();
        
        if(ConfigManager.getInstance().isPreloadOnStartup()) {
            CacheManager.getInstance().doPreload();
        }
    }
}

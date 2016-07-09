package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ManagerLoader implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ManagerLoader.class);
    
    @SuppressWarnings("rawtypes")
    private static List<Class> managerClazzes = new ArrayList<Class>();
    static {
        loadManagerClazz();
    }
    
    private static void loadManagerClazz() {
        managerClazzes.add(CacheManager.class);
        managerClazzes.add(ConfigManager.class);
        managerClazzes.add(ConsoleManager.class);
        managerClazzes.add(DataManager.class);
        managerClazzes.add(EventManager.class);
        managerClazzes.add(IdeaManager.class);
        managerClazzes.add(ItemManager.class);
        managerClazzes.add(MarkManager.class);
        managerClazzes.add(SecManager.class);
        managerClazzes.add(UserManager.class);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        EventManager.getInstance().shutdown();
        
        logger.info("Manager loader context destroyed.");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("Manager loader context initialized.");
        
        for(Class clazz : managerClazzes) {
            try {
                Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
                getInstanceMethod.invoke(null);
                logger.info("Instantiated class [" + clazz.getCanonicalName() + "]");
            }
            catch(Exception e) {
                logger.error("failed to instantiate manager class", e);
            }
        }
        
        if(ConfigManager.getInstance().isPreloadOnStartup()) {
            CacheManager.getInstance().doPreload();
        }
        
        EventManager.getInstance().start();
    }
}

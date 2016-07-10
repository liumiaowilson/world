package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class ManagerLoader implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ManagerLoader.class);
    
    @SuppressWarnings("rawtypes")
    private static List<Class> managerClazzes = new ArrayList<Class>();
    private static List<Object> managers = new ArrayList<Object>();
    static {
        loadManagerClazz();
    }
    
    private static void loadManagerClazz() {
        managerClazzes.add(ActionManager.class);
        managerClazzes.add(CacheManager.class);
        managerClazzes.add(CharManager.class);
        managerClazzes.add(ConfigManager.class);
        managerClazzes.add(ConsoleManager.class);
        managerClazzes.add(DataManager.class);
        managerClazzes.add(EventManager.class);
        managerClazzes.add(ExpManager.class);
        managerClazzes.add(IdeaManager.class);
        managerClazzes.add(ItemManager.class);
        managerClazzes.add(MarkManager.class);
        managerClazzes.add(MonitorManager.class);
        managerClazzes.add(ScheduleManager.class);
        managerClazzes.add(ScriptManager.class);
        managerClazzes.add(SecManager.class);
        managerClazzes.add(StatsManager.class);
        managerClazzes.add(UserManager.class);
    }
    
    @SuppressWarnings("rawtypes")
    public static List<Class> getManagerClazzes() {
        return managerClazzes;
    }
    
    public static List<Object> getManagers() {
        return managers;
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        for(Object manager : managers) {
            if(manager instanceof ManagerLifecycle) {
                ManagerLifecycle lifecycle = (ManagerLifecycle)manager;
                lifecycle.shutdown();
                logger.info(lifecycle.getClass().getSimpleName() + " shut down.");
            }
        }
        
        logger.info("Manager loader context destroyed.");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("Manager loader context initialized.");
        
        for(Class clazz : managerClazzes) {
            try {
                Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
                Object manager = getInstanceMethod.invoke(null);
                managers.add(manager);
                logger.info("Instantiated class [" + clazz.getCanonicalName() + "]");
            }
            catch(Exception e) {
                logger.error("failed to instantiate manager class", e);
            }
        }
        
        if(ConfigManager.getInstance().isPreloadOnStartup()) {
            CacheManager.getInstance().doPreload();
        }
        
        for(Object manager : managers) {
            if(manager instanceof ManagerLifecycle) {
                ManagerLifecycle lifecycle = (ManagerLifecycle)manager;
                lifecycle.start();
                logger.info(lifecycle.getClass().getSimpleName() + " started.");
            }
        }
    }
}

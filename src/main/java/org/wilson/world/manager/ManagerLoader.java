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
        managerClazzes.add(DAOManager.class);
        
        managerClazzes.add(AccountManager.class);
        managerClazzes.add(ActionManager.class);
        managerClazzes.add(ActionParamManager.class);
        managerClazzes.add(CacheManager.class);
        managerClazzes.add(CharManager.class);
        managerClazzes.add(ConfigManager.class);
        managerClazzes.add(ContactManager.class);
        managerClazzes.add(ContactAttrManager.class);
        managerClazzes.add(ContactAttrDefManager.class);
        managerClazzes.add(ConsoleManager.class);
        managerClazzes.add(ContextManager.class);
        managerClazzes.add(DataManager.class);
        managerClazzes.add(DiceManager.class);
        managerClazzes.add(DocumentManager.class);
        managerClazzes.add(ErrorInfoManager.class);
        managerClazzes.add(EventManager.class);
        managerClazzes.add(ExpManager.class);
        managerClazzes.add(ExpenseItemManager.class);
        managerClazzes.add(ExtManager.class);
        managerClazzes.add(FaqManager.class);
        managerClazzes.add(HabitManager.class);
        managerClazzes.add(HabitTraceManager.class);
        managerClazzes.add(HumorPatternManager.class);
        managerClazzes.add(IdeaManager.class);
        managerClazzes.add(ImaginationItemManager.class);
        managerClazzes.add(InventoryItemManager.class);
        managerClazzes.add(ItemManager.class);
        managerClazzes.add(JournalManager.class);
        managerClazzes.add(MarkManager.class);
        managerClazzes.add(MonitorManager.class);
        managerClazzes.add(NotifyManager.class);
        managerClazzes.add(NPCManager.class);
        managerClazzes.add(PostManager.class);
        managerClazzes.add(QueryManager.class);
        managerClazzes.add(QuestDefManager.class);
        managerClazzes.add(QuestManager.class);
        managerClazzes.add(QuoteManager.class);
        managerClazzes.add(RewardManager.class);
        managerClazzes.add(RomanceManager.class);
        managerClazzes.add(RomanceFactorManager.class);
        managerClazzes.add(ScheduleManager.class);
        managerClazzes.add(ScenarioManager.class);
        managerClazzes.add(ScriptManager.class);
        managerClazzes.add(SecManager.class);
        managerClazzes.add(ShopManager.class);
        managerClazzes.add(SkillDataManager.class);
        managerClazzes.add(StarManager.class);
        managerClazzes.add(StatsManager.class);
        managerClazzes.add(StatusManager.class);
        managerClazzes.add(TaskManager.class);
        managerClazzes.add(TaskAttrManager.class);
        managerClazzes.add(TaskAttrDefManager.class);
        managerClazzes.add(TaskAttrRuleManager.class);
        managerClazzes.add(TaskTemplateManager.class);
        managerClazzes.add(TaskSeedManager.class);
        managerClazzes.add(TaskFollowerManager.class);
        managerClazzes.add(TaskTagManager.class);
        managerClazzes.add(ThreadPoolManager.class);
        managerClazzes.add(TickManager.class);
        managerClazzes.add(TrainerSkillManager.class);
        managerClazzes.add(URLManager.class);
        managerClazzes.add(UserManager.class);
        managerClazzes.add(UserItemDataManager.class);
        managerClazzes.add(UserSkillManager.class);
        managerClazzes.add(VariableManager.class);
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
        try {
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
                logger.info("Start preloading...");
                CacheManager.getInstance().doPreload();
            }
            
            for(Object manager : managers) {
                if(manager instanceof ManagerLifecycle) {
                    ManagerLifecycle lifecycle = (ManagerLifecycle)manager;
                    lifecycle.start();
                    logger.info(lifecycle.getClass().getSimpleName() + " started.");
                }
            }
            
            ConsoleManager.getInstance().notifyStarted();
        }
        catch(Exception e) {
            logger.error(e);
        }
    }
}

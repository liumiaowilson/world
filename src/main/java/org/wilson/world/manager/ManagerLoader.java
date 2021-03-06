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
        managerClazzes.add(ActivityManager.class);
        managerClazzes.add(AliasManager.class);
        managerClazzes.add(AlgorithmManager.class);
        managerClazzes.add(AlgorithmProblemManager.class);
        managerClazzes.add(AnchorManager.class);
        managerClazzes.add(ArticleManager.class);
        managerClazzes.add(ArticleSpeedInfoManager.class);
        managerClazzes.add(ArtifactManager.class);
        managerClazzes.add(BackupManager.class);
        managerClazzes.add(BalanceManager.class);
        managerClazzes.add(BeautyManager.class);
        managerClazzes.add(BehaviorManager.class);
        managerClazzes.add(BehaviorDefManager.class);
        managerClazzes.add(BodyLanguageManager.class);
        managerClazzes.add(BookmarkManager.class);
        managerClazzes.add(CacheManager.class);
        managerClazzes.add(CharManager.class);
        managerClazzes.add(ChartManager.class);
        managerClazzes.add(ChatManager.class);
        managerClazzes.add(ChatSkillManager.class);
        managerClazzes.add(ChecklistManager.class);
        managerClazzes.add(ChecklistDefManager.class);
        managerClazzes.add(ClipManager.class);
        managerClazzes.add(CloudStorageManager.class);
        managerClazzes.add(CloudStorageDataManager.class);
        managerClazzes.add(CodeLanguageManager.class);
        managerClazzes.add(CodeRuleManager.class);
        managerClazzes.add(CodeTemplateManager.class);
        managerClazzes.add(CodeSnippetManager.class);
        managerClazzes.add(ColdReadManager.class);
        managerClazzes.add(CommandManager.class);
        managerClazzes.add(ConfigManager.class);
        managerClazzes.add(ContactManager.class);
        managerClazzes.add(ContactAttrManager.class);
        managerClazzes.add(ContactAttrDefManager.class);
        managerClazzes.add(ConsoleManager.class);
        managerClazzes.add(ContextManager.class);
        managerClazzes.add(ControllerManager.class);
        managerClazzes.add(CountdownManager.class);
        managerClazzes.add(CreativityManager.class);
        managerClazzes.add(DataManager.class);
        managerClazzes.add(DataFileManager.class);
        managerClazzes.add(DesignPatternManager.class);
        managerClazzes.add(DetailManager.class);
        managerClazzes.add(DiceManager.class);
        managerClazzes.add(DocumentManager.class);
        managerClazzes.add(DownloadManager.class);
        managerClazzes.add(DressManager.class);
        managerClazzes.add(EmotionManager.class);
        managerClazzes.add(EndPointManager.class);
        managerClazzes.add(EntityDefManager.class);
        managerClazzes.add(EntityManager.class);
        managerClazzes.add(ErrorInfoManager.class);
        managerClazzes.add(EtiquetteManager.class);
        managerClazzes.add(EventManager.class);
        managerClazzes.add(ExpManager.class);
        managerClazzes.add(ExpenseItemManager.class);
        managerClazzes.add(ExpressionManager.class);
        managerClazzes.add(ExtManager.class);
        managerClazzes.add(FaceReadManager.class);
        managerClazzes.add(FaqManager.class);
        managerClazzes.add(FashionManager.class);
        managerClazzes.add(FeedManager.class);
        managerClazzes.add(FestivalDataManager.class);
        managerClazzes.add(FilterManager.class);
        managerClazzes.add(FlashCardManager.class);
        managerClazzes.add(FlashCardSetManager.class);
        managerClazzes.add(FoodManager.class);
        managerClazzes.add(FormManager.class);
        managerClazzes.add(FraudManager.class);
        managerClazzes.add(GoalManager.class);
        managerClazzes.add(GoalDefManager.class);
        managerClazzes.add(GymManager.class);
        managerClazzes.add(HabitManager.class);
        managerClazzes.add(HabitTraceManager.class);
        managerClazzes.add(HealthManager.class);
        managerClazzes.add(HoopManager.class);
        managerClazzes.add(HopperManager.class);
        managerClazzes.add(HopperDataManager.class);
        managerClazzes.add(HowToManager.class);
        managerClazzes.add(HumorManager.class);
        managerClazzes.add(HumorPatternManager.class);
        managerClazzes.add(HungerManager.class);
        managerClazzes.add(IdeaManager.class);
        managerClazzes.add(ImaginationItemManager.class);
        managerClazzes.add(ImageManager.class);
        managerClazzes.add(ImageListManager.class);
        managerClazzes.add(ImageSetManager.class);
        managerClazzes.add(InterviewManager.class);
        managerClazzes.add(InventoryItemManager.class);
        managerClazzes.add(ItemManager.class);
        managerClazzes.add(JarManager.class);
        managerClazzes.add(JavaManager.class);
        managerClazzes.add(JavaFileManager.class);
        managerClazzes.add(JavaClassManager.class);
        managerClazzes.add(JavaObjectManager.class);
        managerClazzes.add(JavaTemplateManager.class);
        managerClazzes.add(JokeManager.class);
        managerClazzes.add(JournalManager.class);
        managerClazzes.add(JsFileManager.class);
        managerClazzes.add(KinoManager.class);
        managerClazzes.add(LinkManager.class);
        managerClazzes.add(LocalFileManager.class);
        managerClazzes.add(ManagerManager.class);
        managerClazzes.add(MangaManager.class);
        managerClazzes.add(MarkManager.class);
        managerClazzes.add(MeditationManager.class);
        managerClazzes.add(MemoryManager.class);
        managerClazzes.add(MenuManager.class);
        managerClazzes.add(MessageManager.class);
        managerClazzes.add(MetaModelManager.class);
        managerClazzes.add(MicroExpressionManager.class);
        managerClazzes.add(MiltonModelManager.class);
        managerClazzes.add(MissionManager.class);
        managerClazzes.add(MonitorManager.class);
        managerClazzes.add(NotesManager.class);
        managerClazzes.add(NotifyManager.class);
        managerClazzes.add(NovelManager.class);
        managerClazzes.add(NovelDocumentManager.class);
        managerClazzes.add(NovelFragmentManager.class);
        managerClazzes.add(NovelRoleManager.class);
        managerClazzes.add(NovelStageManager.class);
        managerClazzes.add(NovelStatManager.class);
        managerClazzes.add(NovelTicketManager.class);
        managerClazzes.add(NovelVariableManager.class);
        managerClazzes.add(NPCManager.class);
        managerClazzes.add(OpenerManager.class);
        managerClazzes.add(PageManager.class);
        managerClazzes.add(PageletManager.class);
        managerClazzes.add(PenaltyManager.class);
        managerClazzes.add(PeriodManager.class);
        managerClazzes.add(PersonalityManager.class);
        managerClazzes.add(ParnManager.class);
        managerClazzes.add(PlanManager.class);
        managerClazzes.add(PlayManager.class);
        managerClazzes.add(PostManager.class);
        managerClazzes.add(ProfileManager.class);
        managerClazzes.add(ProfileDetailManager.class);
        managerClazzes.add(ProxyManager.class);
        managerClazzes.add(PushPullManager.class);
        managerClazzes.add(QueryManager.class);
        managerClazzes.add(QuestDefManager.class);
        managerClazzes.add(QuestManager.class);
        managerClazzes.add(QuizDataManager.class);
        managerClazzes.add(QuoteManager.class);
        managerClazzes.add(ReactionManager.class);
        managerClazzes.add(ReferenceManager.class);
        managerClazzes.add(ReminderManager.class);
        managerClazzes.add(RemoteFileManager.class);
        managerClazzes.add(ReportManager.class);
        managerClazzes.add(RewardManager.class);
        managerClazzes.add(RewriteManager.class);
        managerClazzes.add(RhetoricManager.class);
        managerClazzes.add(RoleManager.class);
        managerClazzes.add(RoleAttrManager.class);
        managerClazzes.add(RoleDetailManager.class);
        managerClazzes.add(RomanceManager.class);
        managerClazzes.add(RomanceFactorManager.class);
        managerClazzes.add(ScheduleManager.class);
        managerClazzes.add(ScenarioManager.class);
        managerClazzes.add(ScriptManager.class);
        managerClazzes.add(SearchManager.class);
        managerClazzes.add(SecManager.class);
        managerClazzes.add(SentenceManager.class);
        managerClazzes.add(ServletManager.class);
        managerClazzes.add(ShopManager.class);
        managerClazzes.add(SignManager.class);
        managerClazzes.add(SkillDataManager.class);
        managerClazzes.add(SleepManager.class);
        managerClazzes.add(SOMPManager.class);
        managerClazzes.add(SpiceManager.class);
        managerClazzes.add(StarManager.class);
        managerClazzes.add(StatsManager.class);
        managerClazzes.add(StatusManager.class);
        managerClazzes.add(StorageManager.class);
        managerClazzes.add(StoryManager.class);
        managerClazzes.add(StorySkillManager.class);
        managerClazzes.add(StrategyManager.class);
        managerClazzes.add(TaskManager.class);
        managerClazzes.add(TaskAttrManager.class);
        managerClazzes.add(TaskAttrDefManager.class);
        managerClazzes.add(TaskAttrRuleManager.class);
        managerClazzes.add(TaskTemplateManager.class);
        managerClazzes.add(TaskSeedManager.class);
        managerClazzes.add(TaskFollowerManager.class);
        managerClazzes.add(TaskTagManager.class);
        managerClazzes.add(TechViewManager.class);
        managerClazzes.add(TerminalManager.class);
        managerClazzes.add(TemplateManager.class);
        managerClazzes.add(ThreadPoolManager.class);
        managerClazzes.add(TickManager.class);
        managerClazzes.add(TodayManager.class);
        managerClazzes.add(TongueTwisterManager.class);
        managerClazzes.add(TrainerSkillManager.class);
        managerClazzes.add(TrickRuleManager.class);
        managerClazzes.add(URLManager.class);
        managerClazzes.add(UserManager.class);
        managerClazzes.add(UserItemDataManager.class);
        managerClazzes.add(UserSkillManager.class);
        managerClazzes.add(VariableManager.class);
        managerClazzes.add(WeaselPhraseManager.class);
        managerClazzes.add(WebManager.class);
        managerClazzes.add(WordManager.class);
        managerClazzes.add(WritingSkillManager.class);
        managerClazzes.add(ZodiacSignManager.class);
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
        
        ThreadPoolManager.getInstance().shutdown();
        
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
            
            ScriptManager.getInstance().notifyStarted();
            
            if(ConfigManager.getInstance().isPreloadOnStartup()) {
                logger.info("Start preloading...");
                CacheManager.getInstance().doPreload();
            }
            
            ThreadPoolManager.getInstance().start();
            
            for(Object manager : managers) {
                if(manager instanceof ManagerLifecycle) {
                    ManagerLifecycle lifecycle = (ManagerLifecycle)manager;
                    logger.info(lifecycle.getClass().getSimpleName() + " is starting...");
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

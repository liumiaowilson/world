package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.TaskSeed;
import org.wilson.world.task.CalendarTaskSeedPattern;
import org.wilson.world.task.DefaultTaskGenerator;
import org.wilson.world.task.TaskGenerator;
import org.wilson.world.task.TaskSeedPattern;
import org.wilson.world.task.TaskSeedWorker;
import org.wilson.world.task.TaskSpawner;

public class TaskSeedManager implements ItemTypeProvider, ManagerLifecycle, EventListener {
    private static final Logger logger = Logger.getLogger(TaskSeedManager.class);
    
    public static final String NAME = "task_seed";
    
    private static TaskSeedManager instance;
    
    private DAO<TaskSeed> dao = null;
    
    private Map<String, TaskSeedPattern> patterns = new HashMap<String, TaskSeedPattern>();
    
    private Map<String, TaskGenerator> generators = new HashMap<String, TaskGenerator>();
    
    @SuppressWarnings("unchecked")
    private TaskSeedManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskSeed.class);
        
        this.loadTaskSeedPatterns();
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        EventManager.getInstance().registerListener(EventType.Login, this);
    }
    
    public static TaskSeedManager getInstance() {
        if(instance == null) {
            instance = new TaskSeedManager();
        }
        return instance;
    }
    
    private void loadTaskSeedPatterns() {
        this.loadTaskSeedPattern(new CalendarTaskSeedPattern());
    }
    
    private void loadTaskSeedPattern(TaskSeedPattern pattern) {
        if(pattern != null) {
            this.patterns.put(pattern.getProtocol(), pattern);
        }
    }
    
    public void createTaskSeed(TaskSeed seed) {
        this.dao.create(seed);
    }
    
    public TaskSeed getTaskSeed(int id) {
        TaskSeed seed = this.dao.get(id);
        if(seed != null) {
            return seed;
        }
        else {
            return null;
        }
    }
    
    public List<TaskSeed> getTaskSeeds() {
        List<TaskSeed> result = new ArrayList<TaskSeed>();
        for(TaskSeed seed : this.dao.getAll()) {
            result.add(seed);
        }
        return result;
    }
    
    public void updateTaskSeed(TaskSeed seed) {
        this.dao.update(seed);
    }
    
    public void deleteTaskSeed(int id) {
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
        return target instanceof TaskSeed;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskSeed seed = (TaskSeed)target;
        return String.valueOf(seed.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public boolean canStart(TimeZone tz, Date date, String pattern) {
        if(date == null) {
            date = new Date();
        }
        if(pattern == null) {
            return false;
        }
        for(String protocol : this.patterns.keySet()) {
            if(pattern.startsWith(protocol)) {
                TaskSeedPattern tsp = this.patterns.get(protocol);
                String value = pattern.substring(protocol.length());
                return tsp.canStart(tz, date, value);
            }
        }
        
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void start() {
        logger.info("Start to load task generators...");
        
        for(TaskSeed seed : this.dao.getAll()) {
            String spawner = seed.spawner;
            int pos = spawner.indexOf(":");
            List<String> args = new ArrayList<String>();
            String spClass = null;
            String argStr = null;
            if(pos > 0) {
                spClass = spawner.substring(0, pos);
                argStr = spawner.substring(pos + 1);
            }
            else {
                spClass = spawner;
                argStr = "";
            }
            if(!StringUtils.isBlank(argStr)) {
                String [] items = argStr.split(" ");
                for(String item : items) {
                    args.add(item.trim());
                }
            }
            
            TaskSpawner ts = null;
            try {
                //try class
                Class spClazz = Class.forName(spClass);
                ts = (TaskSpawner)spClazz.newInstance();
            }
            catch(Exception e) {
                //try action
                ts = (TaskSpawner) ExtManager.getInstance().wrapAction(spClass, TaskSpawner.class);
            }
            
            if(ts == null) {
                logger.warn("Failed to load task spawner for [" + seed.name + "]!");
                continue;
            }
            
            TaskGenerator tg = new DefaultTaskGenerator(seed, ts, args);
            this.generators.put(tg.getName(), tg);
        }
    }

    @Override
    public void shutdown() {
    }
    
    public Map<String, TaskGenerator> getTaskGenerators() {
        return this.generators;
    }
    
    public void generateTasks(TimeZone tz) {
        TaskSeedWorker worker = new TaskSeedWorker(tz);
        ThreadPoolManager.getInstance().execute(worker);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        TimeZone tz = (TimeZone) event.data.get("timezone");
        if(tz != null) {
            this.generateTasks(tz);
        }
    }
}

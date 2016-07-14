package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.MemInit;
import org.wilson.world.dao.MemoryDAO;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.db.DBUtils;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class DAOManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(DAOManager.class);
    private static DAOManager instance;
    
    @SuppressWarnings("rawtypes")
    private Map<Class, DAO> daos = new HashMap<Class, DAO>();
    
    private DAOManager() {
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            boolean success = this.tryDatabase();
            if(!success) {
                ConfigManager.getInstance().setInMemoryModeTemporarily(true);
                logger.info("Failed to connect to the database. Switch to memory mode.");
            }
        }
    }
    
    public static DAOManager getInstance() {
        if(instance == null) {
            instance = new DAOManager();
        }
        return instance;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DAO getCachedDAO(Class itemClass) {
        DAO dao = this.getDAO(itemClass);
        Cache cache = new DefaultCache(dao.getItemTableName(), true);
        CachedDAO ret = new CachedDAO(cache, dao);
        ret.init();
        logger.info("Load initialized cached DAO for [" + itemClass.getCanonicalName() + "].");
        return ret;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DAO getDAO(Class itemClass) {
        if(itemClass == null) {
            return null;
        }
        DAO ret = this.daos.get(itemClass);
        if(ret == null) {
            if(!ConfigManager.getInstance().isInMemoryMode()) {
                String itemName = itemClass.getSimpleName();
                String daoClassName = "org.wilson.world.dao." + itemName + "DAO";
                try {
                    Class daoClass = Class.forName(daoClassName);
                    ret = (DAO)daoClass.newInstance();
                    ret.init();
                    logger.info("Load DAO [" + ret.getClass().getCanonicalName() + "]");
                    this.daos.put(itemClass, ret);
                }
                catch(Exception e) {
                    logger.error("failed to get DAO", e);
                }
            }
            else {
                String itemName = itemClass.getSimpleName();
                MemoryDAO dao = new MemoryDAO(itemName);
                dao.init();
                String memInitClassName = "org.wilson.world.dao." + itemName + "MemInit";
                MemInit memInit = null;
                try {
                    Class memInitClass = Class.forName(memInitClassName);
                    memInit = (MemInit) memInitClass.newInstance();
                }
                catch(Exception e) {
                    //default to be none
                }
                if(memInit != null) {
                    memInit.init(dao);
                    for(Object obj : memInit.getQueryTemplates()) {
                        QueryTemplate qt = (QueryTemplate)obj;
                        dao.addQueryTemplate(qt);
                    }
                }
                ret = dao;
                logger.info("Load DAO [" + ret.getClass().getCanonicalName() + "]");
                this.daos.put(itemClass, ret);
            }
        }
        return ret;
    }
    
    public boolean tryDatabase() {
        Connection con = null;
        Statement s = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            s.executeQuery("select * from users;");
            return true;
        }
        catch(Exception e) {
            return false;
        }
        finally {
            DBUtils.closeQuietly(con, s, null);
        }
    }

    @Override
    public void start() {
        if(ConfigManager.getInstance().isInMemoryMode()) {
            MonitorManager.getInstance().addAlert("Memory Mode", "The system is in MEMORY mode, and is not connected to any database.");
        }
    }

    @Override
    public void shutdown() {
    }
}

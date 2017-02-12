package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.ActiveDAO;
import org.wilson.world.dao.ActiveDAOListener;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.DAOUnloadJob;
import org.wilson.world.dao.MemInit;
import org.wilson.world.dao.MemoryDAO;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.lifecycle.ManagerLifecycle;

@SuppressWarnings("rawtypes")
public class DAOManager implements ManagerLifecycle, JavaExtensionListener<ActiveDAO> {
    private static final Logger logger = Logger.getLogger(DAOManager.class);
    private static DAOManager instance;
    
    private Map<Class, DAO> daos = new HashMap<Class, DAO>();
    
    private List<CachedDAO> cachedDAOs = new ArrayList<CachedDAO>();
    
	private Map<String, ActiveDAO> activeDaos = new HashMap<String, ActiveDAO>();
	
	private List<ActiveDAOListener> listeners = new ArrayList<ActiveDAOListener>();
    
    private DAOManager() {
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            boolean success = this.tryDatabase();
            if(!success) {
                ConfigManager.getInstance().setInMemoryModeTemporarily(true);
                logger.info("Failed to connect to the database. Switch to memory mode.");
            }
        }
        
        ExtManager.getInstance().addJavaExtensionListener(this);
    }
    
    public static DAOManager getInstance() {
        if(instance == null) {
            instance = new DAOManager();
        }
        return instance;
    }
    
    public void addActiveDAOListener(ActiveDAOListener listener) {
    	if(listener != null) {
    		this.listeners.add(listener);
    	}
    }
    
    public void removeActiveDAOListener(ActiveDAOListener listener) {
    	if(listener != null) {
    		this.listeners.remove(listener);
    	}
    }
    
    public List<CachedDAO> getCachedDAOs() {
        return this.cachedDAOs;
    }
    
    @SuppressWarnings("unchecked")
	public DAO getCachedDAO(DAO dao) {
    	Cache cache = new DefaultCache(dao.getItemTableName(), true);
        CachedDAO ret = new CachedDAO(cache, dao);
        ret.init();
        logger.info("Load initialized cached DAO for [" + dao.getItemTableName() + "].");
        this.cachedDAOs.add(ret);
        return ret;
    }
    
    public DAO getCachedDAO(Class itemClass) {
        DAO dao = this.getDAO(itemClass);
        return this.getCachedDAO(dao);
    }
    
    @SuppressWarnings({ "unchecked" })
    public DAO getDAO(Class itemClass) {
        if(itemClass == null) {
            return null;
        }
        DAO ret = this.daos.get(itemClass);
        if(ret == null) {
        	ret = this.activeDaos.get(itemClass.getCanonicalName());
        }
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
        
        ScheduleManager.getInstance().addJob(new DAOUnloadJob());
    }

    @Override
    public void shutdown() {
    }
    
    public String exportData() {
        StringBuffer sb = new StringBuffer();
        
        for(DAO dao : this.daos.values()) {
            sb.append(dao.export());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public void importData(String data) {
        if(StringUtils.isBlank(data)) {
            return;
        }
        
        List<String> names = new ArrayList<String>();
        for(DAO dao : this.daos.values()) {
            names.add(dao.getItemTableName());
        }
        
        Connection con = null;
        Statement s = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            
            for(String name : names) {
                String sql = "truncate table " + name + ";";
                if(logger.isDebugEnabled()) {
                    logger.debug("Executing: " + sql);
                }
                s.execute(sql);
            }
            
            String [] lines = data.trim().split("\\);");
            for(String line : lines) {
                line = line.trim() + ");";
                if(logger.isDebugEnabled()) {
                    logger.debug("Executing: " + line);
                }
                try {
                    s.execute(line);
                }
                catch(Exception e) {
                    logger.error(e);
                }
            }
        }
        catch(Exception e) {
            logger.error(e);
            throw new DataException(e.getMessage());
        }
        finally {
            DBUtils.closeQuietly(con, s, null);
        }
    }
    
    public ActiveDAO getActiveDAO(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	return this.activeDaos.get(name);
    }
    
    public List<ActiveDAO> getActiveDAOs() {
    	return new ArrayList<ActiveDAO>(this.activeDaos.values());
    }

	@Override
	public Class<ActiveDAO> getExtensionClass() {
		return ActiveDAO.class;
	}

	@Override
	public void created(ActiveDAO t) {
		if(t != null && t.getModelClassName() != null) {
			t.init();
			this.activeDaos.put(t.getModelClassName(), t);
			
			for(ActiveDAOListener listener : listeners) {
				listener.created(t);
			}
		}
	}

	@Override
	public void removed(ActiveDAO t) {
		if(t != null) {
			this.activeDaos.remove(t.getModelClassName());
			
			for(ActiveDAOListener listener : listeners) {
				listener.removed(t);
			}
		}
	}
}

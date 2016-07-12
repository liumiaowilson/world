package org.wilson.world.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;

public class DAOManager {
    private static final Logger logger = Logger.getLogger(DAOManager.class);
    private static DAOManager instance;
    
    @SuppressWarnings("rawtypes")
    private Map<Class, DAO> daos = new HashMap<Class, DAO>();
    
    private DAOManager() {
        
    }
    
    public static DAOManager getInstance() {
        if(instance == null) {
            instance = new DAOManager();
        }
        return instance;
    }
    
    @SuppressWarnings("rawtypes")
    public DAO getDAO(Class itemClass) {
        if(itemClass == null) {
            return null;
        }
        DAO ret = this.daos.get(itemClass);
        if(ret == null) {
            String itemName = itemClass.getSimpleName();
            String daoClassName = "org.wilson.world.dao." + itemName + "DAO";
            try {
                Class daoClass = Class.forName(daoClassName);
                ret = (DAO)daoClass.newInstance();
                this.daos.put(itemClass, ret);
            }
            catch(Exception e) {
                logger.error("failed to get DAO", e);
            }
        }
        return ret;
    }
}

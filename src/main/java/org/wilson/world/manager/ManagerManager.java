package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.ActiveDAO;
import org.wilson.world.dao.ActiveDAOListener;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class ManagerManager implements JavaExtensionListener<ActiveManager>, ManagerLifecycle, ActiveDAOListener {
	private static final Logger logger = Logger.getLogger(ManagerManager.class);
	
	private static ManagerManager instance;
	
	private Map<String, ActiveManager> managers = new HashMap<String, ActiveManager>();
	
	private ManagerManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
		
		DAOManager.getInstance().addActiveDAOListener(this);
	}
	
	public static ManagerManager getInstance() {
		if(instance == null) {
			instance = new ManagerManager();
		}
		
		return instance;
	}

	@Override
	public Class<ActiveManager> getExtensionClass() {
		return ActiveManager.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void created(ActiveManager t) {
		if(t != null) {
			start(t);
			
			String modelClassName = t.getModelClassName();
			ActiveDAO dao = DAOManager.getInstance().getActiveDAO(modelClassName);
			if(dao != null) {
				t.init(DAOManager.getInstance().getCachedDAO(dao));
			}
			
			managers.put(t.getName(), t);
		}
	}

	@Override
	public void removed(ActiveManager t) {
		if(t != null) {
			shutdown(t);
			
			managers.remove(t.getName());
		}
	}
	
	public List<ActiveManager> getActiveManagers() {
		return new ArrayList<ActiveManager>(this.managers.values());
	}
	
	public ActiveManager getActiveManager(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.managers.get(name);
	}

	private void start(ActiveManager manager) {
		if(manager instanceof ManagerLifecycle) {
			((ManagerLifecycle)manager).start();
			logger.info("Started active manager [" + manager.getName() + "]");
		}
	}
	
	@Override
	public void start() {
		for(ActiveManager manager : this.getActiveManagers()) {
			start(manager);
		}
	}
	
	private void shutdown(ActiveManager manager) {
		if(manager instanceof ManagerLifecycle) {
			((ManagerLifecycle)manager).shutdown();
			logger.info("Shut down active manager [" + manager.getName() + "]");
		}
	}

	@Override
	public void shutdown() {
		for(ActiveManager manager : this.getActiveManagers()) {
			shutdown(manager);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public ActiveManager getActiveManager(ActiveDAO dao) {
		if(dao == null) {
			return null;
		}
		
		String className = dao.getModelClassName();
		for(ActiveManager manager : managers.values()) {
			if(className.equals(manager.getModelClassName())) {
				return manager;
			}
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void created(ActiveDAO dao) {
		ActiveManager manager = this.getActiveManager(dao);
		if(manager != null) {
			manager.init(DAOManager.getInstance().getCachedDAO(dao));
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removed(ActiveDAO dao) {
		ActiveManager manager = this.getActiveManager(dao);
		if(manager != null) {
			manager.init(null);
		}
	}
}

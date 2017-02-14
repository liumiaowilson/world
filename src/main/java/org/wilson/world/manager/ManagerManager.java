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
	
	private List<ActiveManagerListener> listeners = new ArrayList<ActiveManagerListener>();
	
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
	
	public void addActiveManagerListener(ActiveManagerListener listener) {
		if(listener != null) {
			this.listeners.add(listener);
		}
	}
	
	public void removeActiveManagerListener(ActiveManagerListener listener) {
		if(listener != null) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public Class<ActiveManager> getExtensionClass() {
		return ActiveManager.class;
	}

	@Override
	public void created(ActiveManager t) {
		if(t != null) {
			start(t);
			
			this.init(t, null);
			
			managers.put(t.getName(), t);
			
			for(ActiveManagerListener listener : this.listeners) {
				listener.created(t);
			}
		}
	}

	@Override
	public void removed(ActiveManager t) {
		if(t != null) {
			shutdown(t);
			
			managers.remove(t.getName());
			
			for(ActiveManagerListener listener : this.listeners) {
				listener.removed(t);
			}
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
		this.init(null, dao);
	}
	
	@SuppressWarnings("rawtypes")
	public void init(ActiveManager manager, ActiveDAO dao) {
		if(manager == null && dao == null) {
			return;
		}
		
		if(manager == null) {
			manager = this.getActiveManager(dao);
			if(manager == null) {
				return;
			}
		}
		
		if(dao == null) {
			String modelClassName = manager.getModelClassName();
			dao = DAOManager.getInstance().getActiveDAO(modelClassName);
			if(dao == null) {
				return;
			}
		}
		
		manager.init(DAOManager.getInstance().getCachedDAO(dao));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removed(ActiveDAO dao) {
	}
}

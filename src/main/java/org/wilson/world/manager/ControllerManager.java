package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.controller.AbstractController;
import org.wilson.world.controller.Controller;
import org.wilson.world.controller.DemoController;
import org.wilson.world.controller.PageController;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.model.Page;
import org.wilson.world.servlet.ControllerServlet;

public class ControllerManager implements JavaExtensionListener<AbstractController> {
	private static ControllerManager instance;
	
	private static int GLOBAL_ID = 1;
	
	private Map<String, Controller> controllers = new HashMap<String, Controller>();
	
	private PageController pageController = new PageController();
	
	private ControllerManager() {
		this.loadSystemControllers();
	}
	
	private void loadSystemControllers() {
		this.addController(new DemoController());
		
		ExtManager.getInstance().addJavaExtensionListener(this);
	}
	
	public void addController(Controller controller) {
		if(controller != null && controller.getName() != null) {
			controller.setId(GLOBAL_ID++);
			this.controllers.put(controller.getName(), controller);
		}
	}
	
	public void removeController(Controller controller) {
		if(controller != null && controller.getName() != null) {
			this.controllers.remove(controller.getName());
		}
	}
	
	public static ControllerManager getInstance() {
		if(instance == null) {
			instance = new ControllerManager();
		}
		
		return instance;
	}

	@Override
	public Class<AbstractController> getExtensionClass() {
		return AbstractController.class;
	}

	@Override
	public void created(AbstractController t) {
		this.addController(t);
	}

	@Override
	public void removed(AbstractController t) {
		this.removeController(t);
	}
	
	public List<Controller> getControllers() {
		return new ArrayList<Controller>(this.controllers.values());
	}
	
	public Controller getController(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.controllers.get(name);
	}
	
	public Controller getController(int id) {
		for(Controller controller : this.controllers.values()) {
			if(controller.getId() == id) {
				return controller;
			}
		}
		
		return null;
	}
	
	public Controller getControllerByUri(String uri) {
		if(StringUtils.isBlank(uri)) {
			return null;
		}
		
		for(Controller controller : this.controllers.values()) {
			if(uri.equals(controller.getUri())) {
				return controller;
			}
		}
		
		return null;
	}
	
	public PageController getPageControllerByUri(String uri) {
		if(StringUtils.isBlank(uri)) {
			return null;
		}
		
		String pageName = null;
		if(uri.startsWith(ControllerServlet.SECURE_PREFIX)) {
			this.pageController.setSecure(true);
			pageName = uri.substring(ControllerServlet.SECURE_PREFIX.length());
		}
		else if(uri.startsWith(ControllerServlet.INSECURE_PREFIX)) {
			this.pageController.setSecure(false);
			pageName = uri.substring(ControllerServlet.INSECURE_PREFIX.length());
		}
		
		if(pageName == null) {
			return null;
		}
		
		Page page = PageManager.getInstance().getPage(pageName);
		if(page == null) {
			return null;
		}
		
		this.pageController.setPage(page);
		
		return this.pageController;
	}
}

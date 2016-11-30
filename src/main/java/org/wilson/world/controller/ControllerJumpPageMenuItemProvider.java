package org.wilson.world.controller;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.ControllerManager;
import org.wilson.world.manager.URLManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class ControllerJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "a";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(Controller controller : ControllerManager.getInstance().getControllers()) {
			MenuItem item = new MenuItem();
			item.id = controller.getName();
			item.role = MenuItemRole.Menu;
			item.link = "window.location.href='" + URLManager.getInstance().getBaseUrl() + controller.getUri() + "'";
			items.put(item.id, item);
		}
		
		return items;
	}

}

package org.wilson.world.form;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.FormManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class FormJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "form";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(Form form : FormManager.getInstance().getForms()) {
			MenuItem item = new MenuItem();
			item.id = form.getName();
			item.role = MenuItemRole.Menu;
			item.link = "javascript:jumpTo('form_execute.jsp?id=" + form.getId() + "')";
			items.put(item.id, item);
		}
		
		return items;
	}

}

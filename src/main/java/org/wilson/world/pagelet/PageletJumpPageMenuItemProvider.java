package org.wilson.world.pagelet;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.PageletManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;
import org.wilson.world.model.Pagelet;

public class PageletJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "p";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(Pagelet pagelet : PageletManager.getInstance().getPagelets(PageletType.Private)) {
            if(pagelet.target != null) {
                continue;
            }
			MenuItem item = new MenuItem();
			item.id = pagelet.name;
			item.role = MenuItemRole.Menu;
			item.link = "javascript:jumpTo('page.jsp?id=" + pagelet.id + "')";
			items.put(item.id, item);
		}
		
		return items;
	}

}

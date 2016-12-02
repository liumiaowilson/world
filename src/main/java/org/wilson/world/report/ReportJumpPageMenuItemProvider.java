package org.wilson.world.report;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.ReportManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class ReportJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "r";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(ReportBuilder builder : ReportManager.getInstance().getReportBuilders()) {
			MenuItem item = new MenuItem();
			item.id = builder.getName();
			item.role = MenuItemRole.Menu;
			item.link = "javascript:jumpTo('report_show.jsp?id=" + builder.getId() + "')";
			items.put(item.id, item);
		}
		
		return items;
	}

}

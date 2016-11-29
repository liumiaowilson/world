package org.wilson.world.chart;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.ChartManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class ChartJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "chrt";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(ChartProvider provider : ChartManager.getInstance().getChartProviders()) {
			MenuItem item = new MenuItem();
			item.id = provider.getName();
			item.role = MenuItemRole.Menu;
			item.link = "javascript:jumpTo('chart_view.jsp?id=" + provider.getId() + "')";
			items.put(item.id, item);
		}
		
		return items;
	}

}

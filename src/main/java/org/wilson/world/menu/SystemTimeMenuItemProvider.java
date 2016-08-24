package org.wilson.world.menu;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.util.FormatUtils;

public class SystemTimeMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public MenuItem getMenuItem() {
        Date date = new Date();
        String current_date = FormatUtils.format(date);
        boolean showCurrentDate = ConfigManager.getInstance().getConfigAsBoolean("header.show.server_time");
        if(showCurrentDate) {
            MenuItem item = new MenuItem();
            item.id = "system_time";
            item.label = current_date;
            item.role = MenuItemRole.Menu;
            item.link = "";
            return item;
        }
        
        return null;
    }

}

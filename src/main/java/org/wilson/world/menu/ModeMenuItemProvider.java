package org.wilson.world.menu;

import java.util.Collections;
import java.util.List;

import org.wilson.world.manager.NotifyManager;

public class ModeMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public MenuItem getMenuItem() {
        String modeStatus = NotifyManager.getInstance().getModeStatus();
        if(modeStatus == null) {
            modeStatus = "";
        }

        MenuItem item = new MenuItem();
        item.id = "system_mode";
        item.label = modeStatus;
        item.role = MenuItemRole.Menu;
        item.link = "";
        
        return item;
    }

}

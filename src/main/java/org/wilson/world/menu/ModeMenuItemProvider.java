package org.wilson.world.menu;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.NotifyManager;

public class ModeMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public MenuItem getMenuItem() {
        String modeStatus = NotifyManager.getInstance().getModeStatus();
        if(StringUtils.isBlank(modeStatus)) {
            modeStatus = "";
        }
        
        String modeLink = NotifyManager.getInstance().getModeLink();
        if(StringUtils.isBlank(modeLink)) {
            modeLink = "";
        }

        MenuItem item = new MenuItem();
        item.id = "system_mode";
        item.label = modeStatus;
        item.role = MenuItemRole.Menu;
        item.link = modeLink;
        
        return item;
    }

}

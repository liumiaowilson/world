package org.wilson.world.menu;

import java.util.Collections;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.model.Context;

public class UserContextMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public MenuItem getMenuItem() {
        Context currentContext = ContextManager.getInstance().getCurrentContext();
        String userHint = "User";
        String userColor = "black";
        if(currentContext != null) {
            userHint = currentContext.name;
            userColor = currentContext.color;
        }

        MenuItem item = new MenuItem();
        item.id = "user_context";
        item.label = userHint;
        item.role = MenuItemRole.MenuGroup;
        item.style = "color: " + userColor;
        
        return item;
    }

}

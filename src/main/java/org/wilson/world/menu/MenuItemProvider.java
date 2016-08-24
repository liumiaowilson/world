package org.wilson.world.menu;

import java.util.List;

public interface MenuItemProvider {
    public List<MenuItem> getMenuItems();
    
    public MenuItem getMenuItem();
}

package org.wilson.world.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    public MenuItem parent;
    
    public String id;
    
    public String label;
    
    /**
     * menu, menu-group, separator, placeholder
     */
    public MenuItemRole role;
    
    public String link;
    
    public List<MenuItem> menus = new ArrayList<MenuItem>();
    
    public String style;
    
    public MenuItemProvider provider;
}

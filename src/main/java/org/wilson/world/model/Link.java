package org.wilson.world.model;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.MenuManager;
import org.wilson.world.menu.MenuItem;

public class Link {
    public int id;
    
    public String name;
    
    public String label;
    
    public String itemType;
    
    public int itemId;
    
    public String menuId;
    
    public String toString() {
        MenuItem item = MenuManager.getInstance().getMenuItem(this.menuId);
        String href = null;
        if(item == null) {
            href = menuId;
        }
        else {
            href = item.link;
        }
        
        if(StringUtils.isBlank(href)) {
            href = "javascript:void(0)";
        }
        
        return "<a href=\"" + href + "\">" + this.label + "</a> ";
    }
}

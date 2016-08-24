package org.wilson.world.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.model.Context;

public class ContextMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<MenuItem>();
        
        List<Context> allContexts = ContextManager.getInstance().getContexts();
        Collections.sort(allContexts, new Comparator<Context>(){
            public int compare(Context c1, Context c2) {
                return c1.name.compareTo(c2.name);
            }
        });
        for(Context context : allContexts) {
            MenuItem item = new MenuItem();
            item.id = "context_" + context.id;
            item.label = context.name;
            item.style = "color: " + context.color;
            item.link = "javascript:setCurrentContext(" + context.id + ")";
            item.role = MenuItemRole.Menu;
            items.add(item);
        }
        
        return items;
    }

    @Override
    public MenuItem getMenuItem() {
        return null;
    }

}

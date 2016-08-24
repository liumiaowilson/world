package org.wilson.world.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.manager.QueryManager;
import org.wilson.world.query.QueryProcessor;

public class QueryMenuItemProvider implements MenuItemProvider {

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> ret = new ArrayList<MenuItem>();
        
        List<QueryProcessor> nav_processors = QueryManager.getInstance().getQueryProcessors();
        Collections.sort(nav_processors, new Comparator<QueryProcessor>(){
            public int compare(QueryProcessor p1, QueryProcessor p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });
        for(QueryProcessor nav_processor : nav_processors) {
            if(!nav_processor.isQuickLink()) {
                continue;
            }
            
            MenuItem item = new MenuItem();
            item.id = "query_execute_" + nav_processor.getID();
            item.label = nav_processor.getName();
            item.role = MenuItemRole.Menu;
            item.link = "javascript:jumpTo('query_execute.jsp?id=" + nav_processor.getID() + "')";
            
            ret.add(item);
        }
        
        return ret;
    }

    @Override
    public MenuItem getMenuItem() {
        return null;
    }

}

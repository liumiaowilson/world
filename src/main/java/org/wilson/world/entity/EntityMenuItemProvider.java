package org.wilson.world.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.manager.EntityManager;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemProvider;
import org.wilson.world.menu.MenuItemRole;

public class EntityMenuItemProvider implements MenuItemProvider {

	@Override
	public List<MenuItem> getMenuItems() {
		List<MenuItem> ret = new ArrayList<MenuItem>();
		
		List<EntityDefinition> defs = EntityManager.getInstance().getEntityDefinitions();
		Collections.sort(defs, new Comparator<EntityDefinition>(){

			@Override
			public int compare(EntityDefinition o1, EntityDefinition o2) {
				return o1.name.compareTo(o2.name);
			}
			
		});
		
		for(EntityDefinition def : defs) {
			MenuItem item = new MenuItem();
            item.id = "entity_" + def.id;
            item.label = def.name;
            item.role = MenuItemRole.Menu;
            item.link = "javascript:jumpTo('entity_list.jsp?type=" + def.name + "')";
            
            ret.add(item);
		}
		
		return ret;
	}
	
	@Override
	public MenuItem getMenuItem() {
		return null;
	}

}

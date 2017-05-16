package org.wilson.world.entity;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.manager.EntityManager;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;

public class EntityJumpPageMenuItemProvider implements JumpPageMenuItemProvider {

	@Override
	public String getName() {
		return "e";
	}

	@Override
	public Map<String, MenuItem> getSingleMenuItems() {
		Map<String, MenuItem> items = new HashMap<String, MenuItem>();
		for(EntityDefinition def : EntityManager.getInstance().getEntityDefinitions()) {
			MenuItem itemList = new MenuItem();
			itemList.id = def.name + "_list";
			itemList.role = MenuItemRole.Menu;
			itemList.link = "javascript:jumpTo('entity_list.jsp?type=" + def.name + "')";
			items.put(itemList.id, itemList);
			
			MenuItem itemNew = new MenuItem();
			itemNew.id = def.name + "_new";
			itemNew.role = MenuItemRole.Menu;
			itemNew.link = "javascript:jumpTo('entity_new.jsp?type=" + def.name + "')";
			items.put(itemNew.id, itemNew);

            MenuItem itemCachedList = new MenuItem();
            itemCachedList.id = def.name + "_cached_list";
            itemCachedList.role = MenuItemRole.Menu;
            itemCachedList.link = "javascript:jumpTo('entity_cached_list.jsp?type=" + def.name + "')";
            items.put(itemCachedList.id, itemCachedList);

            MenuItem itemBackupList = new MenuItem();
            itemBackupList.id = def.name + "_backup_list";
            itemBackupList.role = MenuItemRole.Menu;
            itemBackupList.link = "javascript:jumpTo('entity_backup_list.jsp?type=" + def.name + "')";
            items.put(itemBackupList.id, itemBackupList);
		}
		
		return items;
	}

}

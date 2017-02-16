package org.wilson.world.menu;

import org.wilson.world.java.JavaExtensible;

/**
 * Extensions for updating navbar menus
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Extensible menus", name = "system.menu")
public interface ActiveMenu {
	
	public String getName();
	
	/**
	 * Call menu manager api to update navbar menus.
	 * 
	 */
	public void updateNavbarMenus();
}

package org.wilson.world.menu;

import org.wilson.world.java.JavaExtensible;

/**
 * Extensions for updating toolbar menus
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Extensible toolbar", name = "system.toolbar")
public interface ActiveToolbar {
	
	public String getName();
	
	/**
	 * Call menu manager api to update toolbar menus.
	 * 
	 */
	public void updateToolbarMenus();
}

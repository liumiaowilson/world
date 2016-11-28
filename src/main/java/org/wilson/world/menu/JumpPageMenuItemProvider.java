package org.wilson.world.menu;

import java.util.Map;

import org.wilson.world.java.JavaExtensible;

/**
 * Provided menu items for jump pages
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Provider for jump page menu items", name = "system.jump.menuitem")
public interface JumpPageMenuItemProvider {
	/**
	 * Get the name of the provider
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get single menu items for jump pages
	 * 
	 * @return map of menu id and menu item
	 */
	public Map<String, MenuItem> getSingleMenuItems();
}

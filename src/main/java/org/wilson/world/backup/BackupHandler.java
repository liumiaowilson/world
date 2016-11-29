package org.wilson.world.backup;

import org.wilson.world.java.JavaExtensible;

/**
 * Handle backup files
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Generic back up handler", name = "system.backup")
public interface BackupHandler {

	/**
	 * Check if the path will be accepted in the backup
	 * 
	 * @param path
	 * @return
	 */
	public boolean accept(String path);
}

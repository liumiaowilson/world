package org.wilson.world.manager;

import org.wilson.world.dao.DAO;
import org.wilson.world.java.JavaExtensible;

/**
 * Base for manager extension
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Base for manager extension", name = "system.manager")
public interface ActiveManager {

	/**
	 * Get the name of the manager
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the name of the model class
	 * 
	 * @return
	 */
	public String getModelClassName();
	
	/**
	 * Init with a given dao
	 * 
	 * @param dao
	 */
	@SuppressWarnings("rawtypes")
	public void init(DAO dao);
}

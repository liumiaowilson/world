package org.wilson.world.dao;

import org.wilson.world.java.JavaExtensible;

/**
 * Active dao base for extension
 * 
 * @author mialiu
 *
 * @param <T>
 */
@JavaExtensible(description = "General DAO for extension", name = "system.dao")
public abstract class ActiveDAO<T> extends AbstractDAO<T> {

	/**
	 * Get the targeted model class name
	 * 
	 * @return
	 */
	public abstract String getModelClassName();

}

package org.wilson.world.proxy;

import java.util.List;

/**
 * Provide dynamic proxies
 * 
 * @author mialiu
 *
 */
public interface DynamicProxyProvider {

	/**
	 * Get the id of the provider
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * Set the id of the provider
	 * 
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * Get the name of the provider
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Load dynamic proxies
	 * 
	 * @return
	 */
	public List<DynamicProxy> getProxies();
}

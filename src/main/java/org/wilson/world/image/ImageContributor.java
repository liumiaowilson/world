package org.wilson.world.image;

import java.util.List;

/**
 * Image contributor for different implementations
 * 
 * @author mialiu
 *
 */
public interface ImageContributor {

	public static final String PREFIX_SEPARATOR = ":";
	
	/**
	 * Get image ref names
	 * 
	 * @return
	 */
	public List<String> getNames();
	
	/**
	 * Get image ref
	 * 
	 * @param name
	 * @return
	 */
	public ImageRef getImage(String name);
	
	/**
	 * Get the name prefix
	 * 
	 * @return
	 */
	public String getNamePrefix();
}

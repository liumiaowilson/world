package org.wilson.world.image;

/**
 * Image ref for different implementations
 * 
 * @author mialiu
 *
 */
public interface ImageRef {
	/**
	 * Get the unique name of the image ref
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the url of the image ref
	 * 
	 * @return
	 */
	public String getUrl();
	
	/**
	 * Get the url of the adjusted image ref
	 * 
	 * @param width
	 * @param height
	 * @param adjust
	 * @return
	 */
	public String getUrl(int width, int height, boolean adjust);
}

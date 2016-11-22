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
	 * Set the width of the image ref
	 * 
	 * @param width
	 */
	public void setWidth(int width);
	
	/**
	 * Get the width of the image ref
	 * 
	 * @return
	 */
	public int getWidth();
	
	/**
	 * Set the height of the image ref
	 * 
	 * @param height
	 */
	public void setHeight(int height);
	
	/**
	 * Get the height of the image ref
	 * 
	 * @return
	 */
	public int getHeight();
	
	/**
	 * Set whether to adjust the width and height of the image ref
	 * 
	 * @param adjust
	 */
	public void setAdjust(boolean adjust);
	
	/**
	 * Check whether to adjust the width and height of the image ref
	 * 
	 * @return
	 */
	public boolean isAdjust();
}

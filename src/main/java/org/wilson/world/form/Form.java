package org.wilson.world.form;

/**
 * Represent a form in the page
 * 
 * @author mialiu
 *
 */
public interface Form {
	/**
	 * Set the id of the form
	 * 
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * Get the id of the form
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * Get the name of the form
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the title of the form
	 * 
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Get the description of the form
	 * 
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Get the inputs of the form
	 * 
	 * @return
	 */
	public Inputs getInputs();
	
	/**
	 * Execute the form
	 * 
	 * @param data
	 * @return
	 */
	public String execute(FormData data);
}

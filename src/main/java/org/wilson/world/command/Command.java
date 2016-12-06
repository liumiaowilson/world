package org.wilson.world.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Represent the command
 * 
 * @author mialiu
 *
 */
public interface Command {
	/**
	 * Get the name of the command
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the description of the command
	 * 
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Get the options of the command
	 * 
	 * @return
	 */
	public Options getOptions();
	
	/**
	 * Get the usage of the command
	 * 
	 * @return
	 */
	public String getUsage();
	
	/**
	 * Execute the command
	 * 
	 * @param cmd
	 * @return
	 */
	public String execute(CommandLine cmd);
}

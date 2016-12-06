package org.wilson.world.command;

import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Build system command", name = "system.command")
public abstract class AbstractCommand implements Command {
	private String name;
	private String description;
	private Options options;
	
	@Override
	public String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		if(StringUtils.isBlank(description)) {
			return this.getName();
		}
		else {
			return this.description;
		}
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Options getOptions() {
		if(options == null) {
			options = new Options();
			options = this.buildOptions(options);
		}
		
		return options;
	}

	@Override
	public String getUsage() {
		return this.getName();
	}

	public abstract Options buildOptions(Options options);
}

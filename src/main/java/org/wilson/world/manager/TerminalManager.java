package org.wilson.world.manager;

import org.apache.commons.lang.StringUtils;

public class TerminalManager {
	private static TerminalManager instance;
	
	private TerminalManager() {
		
	}
	
	public static TerminalManager getInstance() {
		if(instance == null) {
			instance = new TerminalManager();
		}
		
		return instance;
	}
	
	public String execute(String line) {
		if(StringUtils.isBlank(line)) {
			return null;
		}
		
		//TODO
		
		return line;
	}
}

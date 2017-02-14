package org.wilson.world.java;

import javax.script.Invocable;

import org.apache.log4j.Logger;

public class Script {
	private static final Logger logger = Logger.getLogger(Script.class);
	
	private Invocable inv = null;
	
	public Script(Invocable inv) {
		this.inv = inv;
	}
	
	
	public Object invoke(String name, Object... args) {
		try {
			return this.inv.invokeFunction(name, args);
		} catch (Exception e) {
			logger.error(e);
			return null;
		} 
	}
}

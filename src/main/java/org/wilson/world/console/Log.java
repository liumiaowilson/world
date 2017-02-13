package org.wilson.world.console;

import org.apache.log4j.Logger;

public class Log {
	private static final Logger logger = Logger.getLogger(Log.class);
	
	private static Log instance;
	
	private Log() {
		
	}
	
	public static Log getInstance() {
		if(instance == null) {
			instance = new Log();
		}
		
		return instance;
	}
	
	public void error(Object message) {
		logger.error(message);
	}
	
	public void error(Object message, Throwable e) {
		logger.error(message, e);
	}
	
	public void warn(Object message) {
		logger.warn(message);
	}
	
	public void info(Object message) {
		logger.info(message);
	}
	
	public void debug(Object message) {
		logger.debug(message);
	}
	
	public void trace(Object message) {
		logger.trace(message);
	}
}

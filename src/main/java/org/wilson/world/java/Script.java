package org.wilson.world.java;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.manager.ScriptManager;

public class Script {
	private static final Logger logger = Logger.getLogger(Script.class);
	
	private Invocable inv = null;
	private String script = null;
	
	public Script(Invocable inv, String script) {
		this.inv = inv;
		this.script = script;
	}
	
	
	public Object invoke(String name, Object... args) {
		try {
			return this.inv.invokeFunction(name, args);
		} catch (Exception e) {
			logger.error("failed to invoke method", e);
            String err = e.getMessage();
            if(e instanceof ScriptException) {
            	err = ScriptManager.getInstance().getErrMessage(script, (ScriptException) e);
            }
            throw new DataException(err);
		} 
	}
}

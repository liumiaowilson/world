package org.wilson.world.model;

import org.wilson.world.manager.JavaFileManager;

public class JavaFile {
    public int id;
    
    public String name;
    
    public String description;
    
    public String source;
    
    public String getSource() {
    	if(!JavaFileManager.getInstance().isLoaded(this)) {
    		JavaFileManager.getInstance().load(this);
    	}
    	
    	return this.source;
    }
}

package org.wilson.world.model;

import org.wilson.world.manager.NovelFragmentManager;

public class NovelFragment {
    public int id;
    
    public String name;
    
    public int stageId;
    
    public String condition;
    
    public String content;
    
    public String preCode;
    
    public String postCode;
    
    public String image;
    
    public boolean isAvailableFor(NovelRole role) {
    	return NovelFragmentManager.getInstance().isAvailableFor(this, role);
    }
}

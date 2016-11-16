package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class NovelDocument {
    public int id;
    
    public String name;
    
    public NovelRole role;
    
    public List<NovelFragment> fragments = new ArrayList<NovelFragment>();
}

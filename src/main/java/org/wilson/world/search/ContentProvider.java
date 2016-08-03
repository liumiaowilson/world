package org.wilson.world.search;

import java.util.List;

public interface ContentProvider {
    public String getName();
    
    public List<Content> search(String text);
}

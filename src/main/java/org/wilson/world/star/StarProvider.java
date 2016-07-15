package org.wilson.world.star;

public interface StarProvider {
    public String getDisplayName(Object target);
    
    public boolean accept(Object target);
    
    public void star(Object target);
    
    public void unstar(Object target);
    
    public boolean isStarred(Object target);
    
    public boolean equals(Object o1, Object o2);
}

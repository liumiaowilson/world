package org.wilson.world.cache;

public interface CacheProvider {
    public String getCacheProviderName();
    
    public boolean canPreload();
    
    public void reloadCache();
}

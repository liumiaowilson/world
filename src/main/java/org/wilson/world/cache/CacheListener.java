package org.wilson.world.cache;

import java.util.List;

public interface CacheListener<V> {
    public void cachePut(V old, V v);
    
    public void cacheDeleted(V v);
    
    public void cacheLoaded(List<V> all);
    
    public void cacheLoading(List<V> old);
}

package org.wilson.world.cache;

import java.util.List;

public interface Cache<K, V> {
    public List<K> getKeys();
    
    public V get(K k);
    
    public List<V> getAll();
    
    public void put(K k, V v);
    
    public void delete(K k);
    
    public void init(CacheLoader<K, V> loader);
    
    public void load();
    
    public void clear();
    
    public void addCacheListener(CacheListener<V> listener);
    
    public void removeCacheListener(CacheListener<V> listener);
    
    public int size();
}

package org.wilson.world.cache;

public interface CacheLoader<K, V> {
    public void load(Cache<K, V> cache);
}

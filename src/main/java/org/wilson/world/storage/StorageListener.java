package org.wilson.world.storage;

import java.util.List;

public interface StorageListener {
    public void created(StorageAsset asset);
    
    public void deleted(StorageAsset asset);
    
    public void reloaded(List<StorageAsset> assets);
}

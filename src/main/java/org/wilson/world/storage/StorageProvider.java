package org.wilson.world.storage;

import java.util.Map;

import org.wilson.world.java.JavaExtensible;

/**
 * Provide storage for storage assets
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Generic storage provider", name = "system.storage")
public interface StorageProvider {

	/**
	 * Get the name of the storage provider
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Sync the storage provider
	 * 
	 * @param start the start id of the storage asset
	 * @return the count of storage assets after sync
	 */
	public int sync(int start) throws Exception;
	
	/**
	 * Get storage assets from this provider
	 * 
	 * @return
	 */
	public Map<Integer, StorageAsset> getStorageAssets();
	
	/**
	 * Get the storage asset
	 * 
	 * @param id
	 * @return
	 */
	public StorageAsset getStorageAsset(int id);
	
	/**
	 * Get the storage asset by name
	 * 
	 * @param name
	 * @return
	 */
	public StorageAsset getStorageAsset(String name);
	
	/**
	 * Get the storage status
	 * 
	 * @return
	 */
	public StorageStatus getStorageStatus();
	
	/**
	 * Create the storage asset
	 * 
	 * @param name
	 * @param url
	 * @param assetId
	 * @return
	 * @throws Exception
	 */
	public StorageAsset createStorageAsset(String name, String url, int assetId) throws Exception;
	
	/**
	 * Delete the storage asset
	 * 
	 * @param asset
	 * @return not null if the deletion fails
	 * @throws Exception
	 */
	public String deleteStorageAsset(StorageAsset asset) throws Exception;
	
	/**
	 * Get image url
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 */
	public String getImageUrl(StorageAsset asset) throws Exception;
	
	/**
	 * Get image url
	 * 
	 * @param asset
	 * @param width
	 * @param height
	 * @param adjust
	 * @return
	 * @throws Exception
	 */
	public String getImageUrl(StorageAsset asset, int width, int height, boolean adjust) throws Exception;
	
	/**
	 * Get storage asset content
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 */
	public String getContent(StorageAsset asset) throws Exception;
	
	/**
	 * Get the used percentage(0 to 100)
	 * 
	 * @return
	 * @throws Exception
	 */
	public double getUsedPercentage() throws Exception;
}

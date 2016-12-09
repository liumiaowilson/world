package org.wilson.world.cloud;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.wilson.world.java.JavaExtensible;

/**
 * Cloud storage service
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Generic cloud storage service", name = "system.cloudstorage")
public interface CloudStorageService {

	/**
	 * Get the name of the service
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Init with parameters
	 * 
	 * @param parameters
	 */
	public void init(Map<String, String> parameters);
	
	/**
	 * Create the cloud storage asset
	 * 
	 * @param path
	 * @param is
	 * @throws Exception
	 */
	public CloudStorageAsset createCloudStorageAsset(String path, InputStream is) throws Exception;
	
	/**
	 * Update the cloud storage asset
	 * 
	 * @param asset
	 * @param is
	 * @throws Exception
	 */
	public CloudStorageAsset updateCloudStorageAsset(String path, InputStream is) throws Exception;
	
	/**
	 * Get the cloud storage asset by id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CloudStorageAsset getCloudStorageAssetById(String id) throws Exception;
	
	/**
	 * Get the cloud storage asset by name
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CloudStorageAsset getCloudStorageAssetByName(String name) throws Exception;
	
	/**
	 * Get all cloud storage assets
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<CloudStorageAsset> getCloudStorageAssets() throws Exception;
	
	/**
	 * Delete cloud storage asset
	 * 
	 * @param id
	 * @throws Exception
	 */
	public boolean deleteCloudStorageAsset(String id) throws Exception;
	
	/**
	 * Get cloud storage asset url
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 */
	public String getPublicUrl(CloudStorageAsset asset) throws Exception;
	
	/**
	 * Get the cloud storage asset image url
	 * 
	 * @param asset
	 * @param width
	 * @param height
	 * @param adjust
	 * @return
	 * @throws Exception
	 */
	public String getImageUrl(CloudStorageAsset asset, int width, int height, boolean adjust) throws Exception;
	
	/**
	 * Sync the cloud storage service
	 * 
	 * @throws Exception
	 */
	public void sync() throws Exception;
	
	/**
	 * Get the used percentage from 0 to 100
	 * 
	 * @return
	 * @throws Exception
	 */
	public double getUsedPercentage() throws Exception;
	
	/**
	 * Get the sample config data
	 * 
	 * @return
	 */
	public String getSampleConfigData();
	
	/**
	 * Get the url of the service
	 * 
	 * @return
	 */
	public String getServiceUrl();
}

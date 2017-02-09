package org.wilson.world.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.file.RemoteFile;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;

public class RemoteFileManager implements StorageListener {
	private static final Logger logger = Logger.getLogger(RemoteFileManager.class);
	
	public static final String FILES_DIR = "/files";
	
	public static final String TMP_FILE_NAME = "remote_file_tmp";
	
	private static RemoteFileManager instance;
	
	private Map<Integer, RemoteFile> files = new HashMap<Integer, RemoteFile>();
	
	private RemoteFileManager() {
		StorageManager.getInstance().addStorageListener(this);
	}
	
	public static RemoteFileManager getInstance() {
		if(instance == null) {
			instance = new RemoteFileManager();
		}
		
		return instance;
	}
	
	public void createRemoteFile(RemoteFile remoteFile, InputStream is) {
		if(remoteFile == null || StringUtils.isBlank(remoteFile.name) || is == null) {
			return;
		}
		
		String name = remoteFile.name;
		if(!name.startsWith("/")) {
			name = "/" + name;
		}
		remoteFile.name = name;
		
		File file = new File(ConfigManager.getInstance().getDataDir(), TMP_FILE_NAME);
		File parent = file.getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			IOUtils.copy(is, fos);
		}
		catch(Exception e) {
			logger.error(e);
		}
		finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		
		name = this.toStorageAssetName(name);
		String ret = null;
		try {
			ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/remote_file/get_file");
		}
		catch(Exception e) {
			logger.error(e);
			throw new DataException(e.getMessage());
		}
		if(ret != null) {
			throw new DataException(ret);
		}
	}
	
	private String toStorageAssetName(String name) {
		return FILES_DIR + name;
	}
	
	public List<RemoteFile> getRemoteFiles() {
		return new ArrayList<RemoteFile>(this.files.values());
	}
	
	public RemoteFile getRemoteFile(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(RemoteFile remoteFile : this.files.values()) {
			if(name.equals(remoteFile.name)) {
				return remoteFile;
			}
		}
		
		return null;
	}
	
	public RemoteFile getRemoteFile(int id) {
		return this.files.get(id);
	}
	
	public void updateRemoteFile(RemoteFile remoteFile, InputStream is) {
		if(remoteFile == null || StringUtils.isBlank(remoteFile.name) || is == null) {
			return;
		}
		
		this.deleteRemoteFile(remoteFile.id);
		this.createRemoteFile(remoteFile, is);
	}
	
	public void deleteRemoteFile(int id) {
		RemoteFile remoteFile = this.files.get(id);
		if(remoteFile == null) {
			return;
		}
		
		String name = this.toStorageAssetName(remoteFile.name);
		String ret = null;
		try {
			ret = StorageManager.getInstance().deleteStorageAsset(name);
		}
		catch(Exception e) {
			logger.error(e);
			throw new DataException(e.getMessage());
		}
		if(ret != null) {
			throw new DataException(ret);
		}
	}
	
	public String getContent(RemoteFile remoteFile) {
		if(remoteFile == null) {
			return null;
		}
		
		String name = this.toStorageAssetName(remoteFile.name);
		StorageAsset asset = StorageManager.getInstance().getStorageAsset(name);
		if(asset == null) {
			return null;
		}
		
		String content = null;
		try {
			content = StorageManager.getInstance().getContent(asset);
		}
		catch(Exception e) {
			logger.error(e);
		}
		
		return content;
	}

	@Override
    public void created(StorageAsset asset) {
        RemoteFile file = this.toRemoteFile(asset);
        if(file != null) {
            this.files.put(file.id, file);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
    	RemoteFile file = this.toRemoteFile(asset);
        if(file != null) {
            this.files.remove(file.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.files.clear();
        
        for(StorageAsset asset : assets) {
        	created(asset);
        }
    }
    
    private boolean accept(StorageAsset asset) {
        String name = asset.name;
        
        if(!name.startsWith(FILES_DIR)) {
            return false;
        }
        
        return true;
    }
    
    private RemoteFile toRemoteFile(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        RemoteFile file = new RemoteFile();
        file.id = asset.id;
        String name = asset.name;
        name = name.substring(FILES_DIR.length());
        file.name = name;
        
        return file;
    }
}

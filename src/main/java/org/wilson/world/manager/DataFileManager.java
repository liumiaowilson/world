package org.wilson.world.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.file.DataFile;
import org.wilson.world.file.LocalFile;
import org.wilson.world.file.RemoteFile;

public class DataFileManager {
	private static DataFileManager instance;
	
	public static final String DEFAULT_FILE_SYNTAX = "text";
	
	private DataFileManager() {
		
	}
	
	public static DataFileManager getInstance() {
		if(instance == null) {
			instance = new DataFileManager();
		}
		
		return instance;
	}
	
	public DataFile newLocalFile(String name, InputStream is) {
		if(StringUtils.isBlank(name) || is == null) {
			return null;
		}
		
		LocalFile localFile = new LocalFile();
		localFile.name = name;
		LocalFileManager.getInstance().createLocalFile(localFile, is);
		
		return localFile;
	}
	
	public DataFile newRemoteFile(String name, InputStream is) {
		if(StringUtils.isBlank(name) || is == null) {
			return null;
		}
		
		RemoteFile remoteFile = new RemoteFile();
		remoteFile.name = name;
		RemoteFileManager.getInstance().createRemoteFile(remoteFile, is);
		
		return remoteFile;
	}
	
	public void updateDataFile(DataFile file, InputStream is) {
		if(file == null || is == null) {
			return;
		}
		
		if(file instanceof LocalFile) {
			LocalFileManager.getInstance().updateLocalFile((LocalFile) file, is);
		}
		else if(file instanceof RemoteFile) {
			RemoteFileManager.getInstance().updateRemoteFile((RemoteFile) file, is);
		}
	}
	
	public void deleteDataFile(DataFile file) {
		if(file == null) {
			return;
		}
		
		if(file instanceof LocalFile) {
			LocalFile localFile = (LocalFile)file;
			LocalFileManager.getInstance().deleteLocalFile(localFile.id);
		}
		else if(file instanceof RemoteFile) {
			RemoteFile remoteFile = (RemoteFile)file;
			RemoteFileManager.getInstance().deleteRemoteFile(remoteFile.id);
		}
	}
	
	public List<DataFile> getDataFiles() {
		List<DataFile> files = new ArrayList<DataFile>();
		files.addAll(LocalFileManager.getInstance().getLocalFiles());
		files.addAll(RemoteFileManager.getInstance().getRemoteFiles());
		
		return files;
	}
	
	public DataFile getDataFile(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		LocalFile localFile = LocalFileManager.getInstance().getLocalFile(name);
		if(localFile != null) {
			return localFile;
		}
		
		return RemoteFileManager.getInstance().getRemoteFile(name);
	}
	
	public String getFileSyntax(String name) {
		if(StringUtils.isBlank(name)) {
			return DEFAULT_FILE_SYNTAX;
		}
		
		int pos = name.lastIndexOf(".");
		if(pos < 0) {
			return DEFAULT_FILE_SYNTAX;
		}
		
		String suffix = name.substring(pos + 1);
		String key = "file.syntax." + suffix;
		
		String syntax = DataManager.getInstance().getValue(key);
		if(StringUtils.isNotBlank(syntax)) {
			return syntax;
		}
		
		syntax = ConfigManager.getInstance().getConfig(key);
		if(StringUtils.isNotBlank(syntax)) {
			return syntax;
		}
		else {
			return DEFAULT_FILE_SYNTAX;
		}
	}
}

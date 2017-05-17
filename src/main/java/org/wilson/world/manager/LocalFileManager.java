package org.wilson.world.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.file.FileBackupHandler;
import org.wilson.world.file.LocalFile;
import org.wilson.world.file.LocalFileInfo;

import com.google.common.base.Charsets;

public class LocalFileManager {
	private static final Logger logger = Logger.getLogger(LocalFileManager.class);
	
	public static final String FILES_DIR = "files";
	
	private static int GLOBAL_ID = 1;
	
	private static LocalFileManager instance;
	
	private Map<Integer, LocalFile> files = new HashMap<Integer, LocalFile>();
    private Map<Integer, LocalFileInfo> backupFiles = new HashMap<Integer, LocalFileInfo>();
    private Map<Integer, LocalFileInfo> cachedFiles = new HashMap<Integer, LocalFileInfo>();
	
	private LocalFileManager() {
		this.loadLocalFiles();
		
		BackupManager.getInstance().addBackupHandler(new FileBackupHandler());
	}

    public Map<Integer, LocalFileInfo> getBackupFiles() {
        return this.backupFiles;
    }

    public Map<Integer, LocalFileInfo> getCachedFiles() {
        return this.cachedFiles;
    }
	
	public String getFilesDir() {
		String dir = ConfigManager.getInstance().getDataDir();
		if(!StringUtils.isBlank(dir)) {
			return dir + FILES_DIR;
		}
		else {
			return FILES_DIR;
		}
	}
	
	private void loadLocalFiles() {
		this.loadLocalFile(new File(this.getFilesDir()));
	}
	
	private void loadLocalFile(File file) {
		if(file == null) {
			return;
		}
		
		if(file.exists()) {
			if(file.isFile()) {
				String root = new File(this.getFilesDir()).getAbsolutePath();
				String path = file.getAbsolutePath();
				String name = path.substring(root.length());
				LocalFile localFile = new LocalFile();
				localFile.id = GLOBAL_ID++;
				localFile.name = name;
				this.files.put(localFile.id, localFile);
			}
			else if(file.isDirectory()) {
				for(File f : file.listFiles()) {
					this.loadLocalFile(f);
				}
			}
		}
	}
	
	public static LocalFileManager getInstance() {
		if(instance == null) {
			instance = new LocalFileManager();
		}
		
		return instance;
	}
	
	public void createLocalFile(LocalFile localFile, InputStream is) {
		if(localFile == null || StringUtils.isBlank(localFile.name) || is == null) {
			return;
		}
		
		String name = localFile.name;
		if(!name.startsWith("/")) {
			name = "/" + name;
		}
		localFile.name = name;
		if(localFile.id == 0) {
			localFile.id = GLOBAL_ID++;
		}
		
		File file = localFile.toFile();
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
		
		this.files.put(localFile.id, localFile);
	}
	
	public List<LocalFile> getLocalFiles() {
		return new ArrayList<LocalFile>(this.files.values());
	}
	
	public LocalFile getLocalFile(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(LocalFile localFile : this.files.values()) {
			if(name.equals(localFile.name)) {
				return localFile;
			}
		}
		
		return null;
	}
	
	public LocalFile getLocalFile(int id) {
		return this.files.get(id);
	}
	
	public void updateLocalFile(LocalFile localFile, InputStream is) {
		if(localFile == null || StringUtils.isBlank(localFile.name) || is == null) {
			return;
		}
		
		LocalFile oldLocalFile = this.files.get(localFile.id);
		if(oldLocalFile == null) {
			return;
		}

        String oldLocalFileInfo = this.cachedFiles.remove(oldLocalFile.id);
        if(oldLocalFileInfo != null) {
            this.backupFiles.put(oldLocalFileInfo.id, oldLocalFileInfo);
        }
		if(oldLocalFile.name.equals(localFile.name)) {
			this.createLocalFile(localFile, is);
		}
		else {
			this.deleteLocalFile(localFile.id);
			this.createLocalFile(localFile, is);
		}
	}
	
	public void deleteLocalFile(int id) {
		LocalFile localFile = this.files.get(id);
		if(localFile == null) {
			return;
		}

        String oldLocalFileInfo = this.cachedFiles.remove(oldLocalFile.id);
        if(oldLocalFileInfo != null) {
            this.backupFiles.put(oldLocalFileInfo.id, oldLocalFileInfo);
        }
		
		File file = localFile.toFile();
		if(file.exists()) {
			boolean ret = file.delete();
			if(ret) {
				this.files.remove(localFile.id);
			}
		}
	}
	
	public String getContent(LocalFile localFile) {
		if(localFile == null) {
			return null;
		}

        LocalFileInfo info = this.cachedFiles.get(localFile.id);
        if(info != null) {
            return info.content;
        }
        else {
            File file = localFile.toFile();
            try {
                content = FileUtils.readFileToString(file, Charsets.UTF_8);
            }
            catch(Exception e) {
                logger.error(e);
            }

            info = new LocalFileInfo();
            info.id = localFile.id;
            info.name = localFile.name;
            info.content = content;
            this.cachedFiles.put(info.id, info);

            return content;
        }
	}
}

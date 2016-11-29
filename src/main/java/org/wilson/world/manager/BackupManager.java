package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.backup.BackupHandler;
import org.wilson.world.java.JavaExtensionListener;

public class BackupManager implements JavaExtensionListener<BackupHandler> {
	private static BackupManager instance;
	
	private List<BackupHandler> handlers = new ArrayList<BackupHandler>();
	
	private BackupManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
	}
	
	public static BackupManager getInstance() {
		if(instance == null) {
			instance = new BackupManager();
		}
		
		return instance;
	}
	
	public void addBackupHandler(BackupHandler handler) {
		if(handler != null) {
			this.handlers.add(handler);
		}
	}
	
	public void removeBackupHandler(BackupHandler handler) {
		if(handler != null) {
			this.handlers.remove(handler);
		}
	}

	@Override
	public Class<BackupHandler> getExtensionClass() {
		return BackupHandler.class;
	}

	@Override
	public void created(BackupHandler t) {
		this.addBackupHandler(t);
	}

	@Override
	public void removed(BackupHandler t) {
		this.removeBackupHandler(t);
	}
	
	public List<BackupHandler> getBackupHandlers() {
		return this.handlers;
	}
	
	public boolean accept(String path) {
		if(StringUtils.isBlank(path)) {
			return false;
		}
		
		for(BackupHandler handler : this.handlers) {
			if(handler.accept(path)) {
				return true;
			}
		}
		
		return false;
	}
}

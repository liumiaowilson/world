package org.wilson.world.file;

import org.wilson.world.backup.BackupHandler;
import org.wilson.world.manager.LocalFileManager;

public class FileBackupHandler implements BackupHandler {

	@Override
	public boolean accept(String path) {
		return path != null && path.startsWith("/" + LocalFileManager.FILES_DIR + "/");
	}

}

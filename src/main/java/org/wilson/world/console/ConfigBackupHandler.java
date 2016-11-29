package org.wilson.world.console;

import org.wilson.world.backup.BackupHandler;
import org.wilson.world.manager.ConfigManager;

public class ConfigBackupHandler implements BackupHandler {

	@Override
	public boolean accept(String path) {
		return ("/" + ConfigManager.CONFIG_OVERRIDE_FILE_NAME).equals(path);
	}

}

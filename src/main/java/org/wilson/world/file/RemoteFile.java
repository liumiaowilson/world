package org.wilson.world.file;

import org.wilson.world.manager.RemoteFileManager;

public class RemoteFile implements DataFile {
	public int id;
	
	public String name;
	
	public String getContent() {
		return RemoteFileManager.getInstance().getContent(this);
	}

	@Override
	public String getName() {
		return name;
	}
}

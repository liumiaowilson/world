package org.wilson.world.file;

import java.io.File;

import org.wilson.world.manager.RemoteFileManager;

public class RemoteFile {
	public int id;
	
	public String name;
	
	public String getContent() {
		return RemoteFileManager.getInstance().getContent(this);
	}
}

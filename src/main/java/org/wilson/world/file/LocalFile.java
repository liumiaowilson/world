package org.wilson.world.file;

import java.io.File;

import org.wilson.world.manager.LocalFileManager;

public class LocalFile implements DataFile{
	public int id;
	
	public String name;
	
	public File toFile() {
		return new File(LocalFileManager.getInstance().getFilesDir() + name);
	}
	
	public String getContent() {
		return LocalFileManager.getInstance().getContent(this);
	}

	@Override
	public String getName() {
		return name;
	}
}

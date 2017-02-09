package org.wilson.world.file;

import java.util.List;

public interface RemoteFileListener {
	public void created(RemoteFile file);
	
	public void deleted(RemoteFile file);
	
	public void reloaded(List<RemoteFile> files);
}

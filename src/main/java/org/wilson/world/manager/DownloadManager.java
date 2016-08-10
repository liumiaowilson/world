package org.wilson.world.manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.lang.StringUtils;

public class DownloadManager {
    private static DownloadManager instance;
    
    private DownloadManager() {
        
    }
    
    public static DownloadManager getInstance() {
        if(instance == null) {
            instance = new DownloadManager();
        }
        
        return instance;
    }
    
    public void download(String url, String path) throws IOException {
        if(StringUtils.isBlank(url) || StringUtils.isBlank(path)) {
            return;
        }
        
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            fos.close();
        }
    }
}

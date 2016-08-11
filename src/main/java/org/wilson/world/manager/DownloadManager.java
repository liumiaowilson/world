package org.wilson.world.manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.util.SizeUtils;
import org.wilson.world.web.WebJobMonitor;

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
    
    public void download(String url, String path, WebJobMonitor monitor) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.connect();
        
        if(connection.getResponseCode() / 100 != 2) {
            throw new DataException("Error response code");
        }
        
        int contentLength = connection.getContentLength();
        if (contentLength < 1) {
            throw new DataException("Error content length");
        }
        
        if(contentLength > 100 * SizeUtils.SIZE_MB) {
            throw new DataException("File is too huge to download");
        }
        
        InputStream is = connection.getInputStream();
        OutputStream os = new FileOutputStream(path);
        
        int bufferSize = 10240;
        int total = contentLength / bufferSize;
        if(monitor != null) {
            monitor.start(total);
        }
        
        byte [] buffer = new byte[bufferSize];
        
        int downloaded = 0;
        while(true) {
            int read = is.read(buffer);
            if(read == -1) {
                break;
            }
            
            os.write(buffer, 0, read);
            
            downloaded += read;
            
            if(monitor != null) {
                if(monitor.isStopRequired()) {
                    monitor.stop();
                    break;
                }
                if(downloaded >= bufferSize) {
                    monitor.progress(1);
                }
            }
            
            if(downloaded >= bufferSize) {
                downloaded -= bufferSize;
            }
        }
        
        is.close();
        os.close();
    }
}

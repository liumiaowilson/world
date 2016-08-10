package org.wilson.world.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class DownloadManagerTest {

    @Test
    public void testDownload() throws IOException {
        DownloadManager.getInstance().download("http://img1.mm131.com/pic/2606/44.jpg", "/Users/mialiu/Downloads/image.jpg");
    }

    @Test
    public void testClip() throws IOException {
        ConfigManager.getInstance();
        URL url = new URL("http://cdn2a.cliphunter.com/mob/25937/2593717_p360.mp4?nvb=20160810102813&nva=20160810142813&ir=-1&sr=1061&int=2506752b&hash=07a5a606196cd47063384");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        
        if(connection.getResponseCode() / 100 != 2) {
            System.out.println("Error code");
            return;
        }
        
        int contentLength = connection.getContentLength();
        if (contentLength < 1) {
            System.out.println("Error content");
            return;
        }
        
        File file = new File("/Users/mialiu/Downloads/test.mp4");
        
        InputStream is = connection.getInputStream();
        OutputStream os = new FileOutputStream(file);
        
        byte [] buffer = new byte[4096];
        int downloaded = 0;
        
        while(true) {
            int read = is.read(buffer);
            if(read == -1) {
                break;
            }
            
            os.write(buffer, 0, read);
            
            downloaded += read;
            
            double pct = 100.0 * downloaded / contentLength;
            System.out.println(pct);
        }
        
        is.close();
        os.close();
    }
}

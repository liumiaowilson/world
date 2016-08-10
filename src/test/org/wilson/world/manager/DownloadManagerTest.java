package org.wilson.world.manager;

import java.io.IOException;

import org.junit.Test;

public class DownloadManagerTest {

    @Test
    public void testDownload() throws IOException {
        DownloadManager.getInstance().download("http://img1.mm131.com/pic/2606/44.jpg", "/Users/mialiu/Downloads/image.jpg");
    }

}

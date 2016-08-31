package org.wilson.world.util;

import java.io.InputStream;

import org.junit.Test;

public class IOUtilsTest {

    @Test
    public void testChecksum() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("Demo.java");
        String checksum = IOUtils.getChecksum(is);
        System.out.println(checksum);
    }

}

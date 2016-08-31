package org.wilson.world.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;

public class IOUtils {
    public static String toString(InputStream is) throws IOException {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        finally {
            if(br != null) {
                br.close();
            }
        }
        
        return sb.toString();
    }
    
    public static String getChecksum(InputStream is) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = is.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        byte[] mdbytes = md.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }
        
        return hexString.toString();
    }
}

package org.wilson.world.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}

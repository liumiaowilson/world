package org.wilson.world.util;

import java.io.File;

public class SizeUtils {
    public static final long SIZE_KB = 1024L;
    
    public static final long SIZE_MB = 1024 * SIZE_KB;
    
    public static String getSizeReadableString(long size) {
        if(size < SIZE_KB) {
            return "Less than 1 K";
        }
        else if(size < SIZE_MB) {
            int result = (int)(size / SIZE_KB);
            return result + " K";
        }
        else {
            int result = (int)(size / SIZE_MB);
            return result + " M";
        }
    }
    
    public static long getSize(File file) {
        if(file == null) {
            return -1;
        }
        
        if(file.isFile()) {
            return file.length();
        }
        else if(file.isDirectory()) {
            long sum = 0;
            for(File f : file.listFiles()) {
                long size = getSize(f);
                if(size > 0) {
                    sum += size;
                }
            }
            return sum;
        }
        
        return -1;
    }
}

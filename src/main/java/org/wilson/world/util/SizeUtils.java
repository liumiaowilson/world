package org.wilson.world.util;

public class SizeUtils {
    public static final long SIZE_MB = 1024 * 1024L;
    
    public static String getSizeReadableString(long size) {
        if(size < SIZE_MB) {
            return "Less than 1 M";
        }
        else {
            int result = (int)(size / SIZE_MB);
            return result + " M";
        }
    }
}

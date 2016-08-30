package org.wilson.world.util;

import java.io.File;

public class FileUtils {
    public static void delete(String path) {
        delete(new File(path));
    }
    
    public static void delete(File file) {
        if(file == null) {
            return;
        }
        
        if(!file.exists()) {
            return;
        }
        
        if(file.isFile()) {
            file.delete();
        }
        else if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                f.delete();
            }
            
            file.delete();
        }
    }
}

package org.wilson.world.manager;

import java.io.File;

import org.wilson.world.clip.ClipInfo;
import org.wilson.world.web.WebJobMonitor;

public class ClipManager {
    private static ClipManager instance;
    
    private String current;
    
    private ClipManager() {
        File dirFile = new File(this.getClipDir());
        if(dirFile.exists()) {
            File [] files = dirFile.listFiles();
            if(files.length == 1) {
                this.current = files[0].getName();
            }
        }
    }
    
    public static ClipManager getInstance() {
        if(instance == null) {
            instance = new ClipManager();
        }
        return instance;
    }
    
    public String getClipDir() {
        return ConfigManager.getInstance().getDataDir() + "clip/";
    }
    
    public void downloadClipInfo(ClipInfo info, WebJobMonitor monitor) throws Exception {
        if(info == null) {
            return;
        }
        
        String dir = this.getClipDir();
        File dirFile = new File(dir);
        if(!dirFile.exists()) {
            dirFile.mkdirs();
        }
        
        try {
            DownloadManager.getInstance().download(info.url, dir + info.name, monitor);
            
            for(File file : dirFile.listFiles()) {
                if(!file.getName().equals(info.name)) {
                    file.delete();
                }
            }
            
            this.current = info.name;
        }
        catch(Exception e) {
            File file = new File(dir + info.name);
            if(file.exists()) {
                file.delete();
            }
            
            throw e;
        }
    }
    
    public String getCurrent() {
        return this.current;
    }
}

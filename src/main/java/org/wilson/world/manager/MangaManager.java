package org.wilson.world.manager;

import java.io.File;

public class MangaManager {
    private static MangaManager instance;
    
    private MangaManager() {
        
    }
    
    public static MangaManager getInstance() {
        if(instance == null) {
            instance = new MangaManager();
        }
        
        return instance;
    }
    
    public String getMangaDir() {
        return ConfigManager.getInstance().getDataDir() + "manga/";
    }
    
    public void ensureMangaDir() {
        File mangaDir = new File(this.getMangaDir());
        
        if(!mangaDir.isDirectory()) {
            mangaDir.delete();
        }
        
        if(!mangaDir.exists()) {
            mangaDir.mkdirs();
        }
    }
    
    public void clean() {
        File mangaDir = new File(this.getMangaDir());
        
        for(File old : mangaDir.listFiles()) {
            old.delete();
        }
    }
    
    public int getCurrentPages() {
        File mangaDir = new File(this.getMangaDir());
        
        String [] files = mangaDir.list();
        return files == null ? 0 : files.length;
    }
}

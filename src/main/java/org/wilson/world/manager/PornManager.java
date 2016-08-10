package org.wilson.world.manager;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.porn.PornInfo;
import org.wilson.world.porn.PornListJob;

public class PornManager {
    public static final String IMAGE_PATH = "image.jpg";
    
    private static PornManager instance;
    
    private PornManager() {
    }
    
    public static PornManager getInstance() {
        if(instance == null) {
            instance = new PornManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public PornInfo randomPorn() {
        List<PornInfo> infos = (List<PornInfo>) WebManager.getInstance().get(PornListJob.PORN_LIST);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public String getPornImagePath(PornInfo info) {
        if(info == null) {
            return null;
        }
        
        return IMAGE_PATH;
    }
    
    public void downloadPorn(PornInfo info) throws IOException {
        if(info == null) {
            return;
        }
        
        if(!StringUtils.isBlank(info.url)) {
            DownloadManager.getInstance().download(info.url, ConfigManager.getInstance().getDataDir() + this.getPornImagePath(info));
        }
    }
}

package org.wilson.world.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.porn.JapanPornListJob;
import org.wilson.world.porn.PornInfo;
import org.wilson.world.porn.PornListJob;

public class PornManager {
    public static final String IMAGE_PATH = "image.jpg";
    
    private List<String> pornTypes = new ArrayList<String>();
    
    private static PornManager instance;
    
    private PornManager() {
        pornTypes.add(PornListJob.PORN_LIST);
        pornTypes.add(JapanPornListJob.JAPAN_PORN_LIST);
    }
    
    public static PornManager getInstance() {
        if(instance == null) {
            instance = new PornManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public PornInfo randomPorn() {
        List<String> availableTypes = new ArrayList<String>();
        for(String type : this.pornTypes) {
            List<PornInfo> infos = (List<PornInfo>) WebManager.getInstance().get(type);
            if(infos != null && !infos.isEmpty()) {
                availableTypes.add(type);
            }
        }
        
        if(availableTypes.isEmpty()) {
            return null;
        }
        
        int t = DiceManager.getInstance().random(availableTypes.size());
        String type = availableTypes.get(t);
        
        List<PornInfo> infos = (List<PornInfo>) WebManager.getInstance().get(type);
        
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

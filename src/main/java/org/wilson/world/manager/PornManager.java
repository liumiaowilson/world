package org.wilson.world.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.porn.PornInfo;

public class PornManager {
    public static final String IMAGE_PATH = "image.jpg";
    
    public static final String PORNS = "porns";
    
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
        List<String> froms = new ArrayList<String>();
        Map<String, List<PornInfo>> porns = (Map<String, List<PornInfo>>) WebManager.getInstance().get(PORNS);
        for(Entry<String, List<PornInfo>> entry : porns.entrySet()) {
            String from = entry.getKey();
            List<PornInfo> infos = entry.getValue();
            if(infos != null && !infos.isEmpty()) {
                froms.add(from);
            }
        }
        
        if(froms.isEmpty()) {
            return null;
        }
        
        int t = DiceManager.getInstance().random(froms.size());
        String from = froms.get(t);
        
        List<PornInfo> infos = porns.get(from);
        
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
    
    @SuppressWarnings("unchecked")
    public void clearPornInfos(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<PornInfo>> porns = (Map<String, List<PornInfo>>) WebManager.getInstance().get(PORNS);
        if(porns != null) {
            List<PornInfo> infos = porns.get(from);
            if(infos != null && !infos.isEmpty()) {
                infos.clear();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void addPornInfo(PornInfo info) {
        if(info == null) {
            return;
        }
        
        if(StringUtils.isBlank(info.from)) {
            return;
        }
        
        Map<String, List<PornInfo>> porns = (Map<String, List<PornInfo>>) WebManager.getInstance().get(PORNS);
        if(porns == null) {
            porns = new HashMap<String, List<PornInfo>>();
            WebManager.getInstance().put(PORNS, porns);
        }
        
        List<PornInfo> infos = porns.get(info.from);
        if(infos == null) {
            infos = new ArrayList<PornInfo>();
            porns.put(info.from, infos);
        }
        infos.add(info);
    }
}

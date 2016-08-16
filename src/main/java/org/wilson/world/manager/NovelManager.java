package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.novel.NovelInfo;
import org.wilson.world.web.WebJob;

public class NovelManager {
    public static final String NOVELS = "novels";
    
    private static NovelManager instance;
    
    private String current;
    
    private Map<Integer, NovelInfo> ids = new ConcurrentHashMap<Integer, NovelInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private NovelInfo selected;
    
    public static final String NOVEL_FILE_NAME = "novel.html";
    
    private NovelManager() {
        
    }
    
    public static NovelManager getInstance() {
        if(instance == null) {
            instance = new NovelManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public NovelInfo getSelected() {
        return selected;
    }

    public void setSelected(NovelInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<NovelInfo>> getNovelInfoMap() {
        Map<String, List<NovelInfo>> map = (Map<String, List<NovelInfo>>) WebManager.getInstance().get(NOVELS);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<NovelInfo>>();
            this.setNovelInfoMap(map);
        }
        
        return map;
    }
    
    public void setNovelInfoMap(Map<String, List<NovelInfo>> map) {
        WebManager.getInstance().put(NOVELS, map);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        List<NovelInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(NovelInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addNovelInfo(NovelInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        List<NovelInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<NovelInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public NovelInfo getNovelInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<NovelInfo> getNovelInfos() {
        return new ArrayList<NovelInfo>(this.ids.values());
    }
    
    public NovelInfo randomNovelInfo() {
        String from = this.current;
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<NovelInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadNovelInfo(NovelInfo info) {
        if(info == null) {
            return;
        }
        
        for(NovelInfo i : this.getNovelInfos()) {
            if(i.html != null) {
                i.html = null;
            }
        }
        
        String from = info.from;
        String jobName = from;
        WebJob job = WebManager.getInstance().getAvailableWebJobByName(jobName);
        if(job == null) {
            job = WebManager.getInstance().getAvailableWebJobByName(jobName + "Job");
        }
        
        if(job == null) {
            return;
        }
        
        this.setSelected(info);
        
        WebManager.getInstance().run(job);
        
        this.setSelected(null);
    }
    
    public String getNovelFileName() {
        return NOVEL_FILE_NAME;
    }
}

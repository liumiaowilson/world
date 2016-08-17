package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.howto.HowToInfo;
import org.wilson.world.howto.HowToListJob;
import org.wilson.world.web.WebJob;

public class HowToManager {
    private static HowToManager instance;
    
    private HowToManager() {
        
    }
    
    public static HowToManager getInstance() {
        if(instance == null) {
            instance = new HowToManager();
        }
        
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public HowToInfo randomwHowToInfo() {
        List<HowToInfo> infos = WebManager.getInstance().getList(HowToListJob.HOW_TO_LIST);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    @SuppressWarnings("unchecked")
    public void loadHowToInfo(HowToInfo info) {
        if(info == null) {
            return;
        }
        
        List<HowToInfo> infos = WebManager.getInstance().getList(HowToListJob.HOW_TO_LIST);
        if(infos != null && !infos.isEmpty()) {
            for(HowToInfo i : infos) {
                if(i.answer != null) {
                    i.answer = null;
                }
            }
        }
        
        WebJob job = WebManager.getInstance().getAvailableWebJobByName(HowToListJob.class.getSimpleName());
        if(job == null) {
            return;
        }
        
        try {
            WebManager.getInstance().put(HowToListJob.HOW_TO_INFO, info);
            WebManager.getInstance().run(job);
        }
        finally {
            WebManager.getInstance().put(HowToListJob.HOW_TO_INFO, null);
        }
    }
}

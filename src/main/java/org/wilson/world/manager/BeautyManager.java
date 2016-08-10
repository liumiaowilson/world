package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.beauty.BeautyInfo;
import org.wilson.world.beauty.BeautyListJob;

public class BeautyManager {
    private static BeautyManager instance;
    
    private BeautyManager() {
    }
    
    public static BeautyManager getInstance() {
        if(instance == null) {
            instance = new BeautyManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public BeautyInfo randomBeauty() {
        List<BeautyInfo> infos = (List<BeautyInfo>) WebManager.getInstance().get(BeautyListJob.BEAUTY_LIST);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
}

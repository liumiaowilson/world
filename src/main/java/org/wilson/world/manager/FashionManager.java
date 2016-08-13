package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.fashion.FashionInfo;
import org.wilson.world.fashion.FashionListJob;

public class FashionManager {
    private static FashionManager instance;
    
    private FashionManager() {
    }
    
    public static FashionManager getInstance() {
        if(instance == null) {
            instance = new FashionManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public FashionInfo randomFashion() {
        List<FashionInfo> infos = (List<FashionInfo>) WebManager.getInstance().get(FashionListJob.FASHION_LIST);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
}

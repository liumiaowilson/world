package org.wilson.world.techview;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.item.DBCleaner;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.TechViewManager;
import org.wilson.world.model.TechView;

public class TechViewDBCleaner implements DBCleaner {
    private static final Logger logger = Logger.getLogger(TechViewDBCleaner.class);

    @Override
    public void clean() {
        List<TechView> views = TechViewManager.getInstance().getTechViews();
        int max = ConfigManager.getInstance().getConfigAsInt("tech_view.max_size", 100);
        Collections.sort(views, new Comparator<TechView>(){

            @Override
            public int compare(TechView o1, TechView o2) {
                return Integer.compare(o1.id, o2.id);
            }
            
        });
        
        int count = 0;
        while(views.size() > max) {
            TechView view = views.remove(0);
            TechViewManager.getInstance().deleteTechView(view.id);
            count++;
        }
        
        logger.info("Cleaned " + count + " tech view(s)");
    }

}

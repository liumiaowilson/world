package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.gym.GymItem;
import org.wilson.world.gym.GymType;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.menu.MenuItem;

import net.sf.json.JSONObject;

public class GymManager implements ManagerLifecycle {
    public static final String GYM_ITEM_KEY = "gym";
    
    private static final Logger logger = Logger.getLogger(GymManager.class);
    
    private static GymManager instance;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, GymItem> items = new HashMap<Integer, GymItem>();
    
    private GymManager() {
    }
    
    public static GymManager getInstance() {
        if(instance == null) {
            instance = new GymManager();
        }
        
        return instance;
    }
    
    public void addGymItem(GymItem item) {
        if(item != null) {
            item.id = GLOBAL_ID++;
            this.items.put(item.id, item);
        }
    }

    @Override
    public void start() {
        logger.info("Start to load gym items ...");
        
        List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
        for(String menuId : menuIds) {
            MenuItem menu = MenuManager.getInstance().getMenuItem(menuId);
            if(menu.data.containsKey(GYM_ITEM_KEY)) {
                JSONObject data = (JSONObject) menu.data.get(GYM_ITEM_KEY);
                if(data != null) {
                    try {
                        String name = data.getString("name");
                        GymType type = GymType.valueOf(data.getString("type"));
                        
                        GymItem item = new GymItem();
                        item.name = name;
                        item.type = type;
                        item.menu = menu;
                        this.addGymItem(item);
                    }
                    catch(Exception e){
                        logger.error(e);
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() {
    }
    
    public List<GymItem> getGymItems() {
        return new ArrayList<GymItem>(this.items.values());
    }
}

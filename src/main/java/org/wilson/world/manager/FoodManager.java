package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.food.FoodInfo;

public class FoodManager {
    public static final String FOODS = "foods";
    
    public static final String FOODS_REMOVED = "foods_removed";
    
    private static FoodManager instance;
    
    private String current;
    
    private Map<Integer, FoodInfo> ids = new ConcurrentHashMap<Integer, FoodInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private FoodInfo selected;
    
    private FoodManager() {
    }
    
    public static FoodManager getInstance() {
        if(instance == null) {
            instance = new FoodManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public FoodInfo getSelected() {
        return selected;
    }

    public void setSelected(FoodInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<FoodInfo>> getFoodInfoMap() {
        Map<String, List<FoodInfo>> map = (Map<String, List<FoodInfo>>) WebManager.getInstance().get(FOODS);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<FoodInfo>>();
            this.setFoodInfoMap(map);
        }
        
        return map;
    }
    
    public void setFoodInfoMap(Map<String, List<FoodInfo>> map) {
        WebManager.getInstance().put(FOODS, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, FoodInfo> getFoodsRemoved() {
        return (Map<String, FoodInfo>) WebManager.getInstance().get(FOODS_REMOVED);
    }
    
    public void setFoodsRemoved(Map<String, FoodInfo> removed) {
        WebManager.getInstance().put(FOODS_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<FoodInfo>> map = this.getFoodInfoMap();
        List<FoodInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(FoodInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addFoodInfo(FoodInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, FoodInfo> removed = this.getFoodsRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<FoodInfo>> map = this.getFoodInfoMap();
        List<FoodInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<FoodInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public FoodInfo getFoodInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<FoodInfo> getFoodInfos() {
        return new ArrayList<FoodInfo>(this.ids.values());
    }
    
    public FoodInfo randomFoodInfo() {
        String from = this.current;
        
        Map<String, List<FoodInfo>> map = this.getFoodInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<FoodInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void removeFoodInfo(FoodInfo info) {
        if(info != null) {
            Map<String, List<FoodInfo>> foods = this.getFoodInfoMap();
            List<FoodInfo> infos = foods.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, FoodInfo> removed = this.getFoodsRemoved();
            if(removed == null) {
                removed = new HashMap<String, FoodInfo>();
                this.setFoodsRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}

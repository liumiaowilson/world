package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.star.StarProvider;
import org.wilson.world.star.StarRewardGiver;

public class StarManager {
    private static StarManager instance;
    
    private List<StarProvider> providers = new ArrayList<StarProvider>();
    @SuppressWarnings("rawtypes")
    private Map<StarProvider, List> stars = new HashMap<StarProvider, List>();
    
    private StarManager() {
        RewardManager.getInstance().addRewardGiver(new StarRewardGiver());
    }
    
    public static StarManager getInstance() {
        if(instance == null) {
            instance = new StarManager();
        }
        return instance;
    }
    
    public StarProvider getStarProvider(Object target) {
        if(target == null) {
            return null;
        }
        StarProvider ret = null;
        for(StarProvider provider : providers) {
            if(provider.accept(target)) {
                ret = provider;
                break;
            }
        }
        return ret;
    }
    
    public void registerStarProvider(StarProvider provider) {
        if(provider != null) {
            if(!this.providers.contains(provider)) {
                this.providers.add(provider);
            }
        }
    }
    
    public void unregisterStarProvider(StarProvider provider) {
        if(provider != null) {
            this.providers.remove(provider);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void process(List candidates) {
        if(candidates == null || candidates.isEmpty()) {
            return;
        }
        
        StarProvider provider = this.getStarProvider(candidates.get(0));
        if(provider == null) {
            return;
        }
        List list = this.stars.get(provider);
        if(list == null) {
            list = new ArrayList();
            this.stars.put(provider, list);
            for(Object candidate : candidates) {
                provider.unstar(candidate);
                if(DiceManager.getInstance().dice(5)) {
                    provider.star(candidate);
                    list.add(candidate);
                }
            }
        }
        else {
            for(Object candidate : candidates) {
                provider.unstar(candidate);
                for(Object item : list) {
                    if(provider.equals(candidate, item)) {
                        provider.star(candidate);
                    }
                }
            }
        }
    }
    
    public void reset() {
        this.stars.clear();
    }
    
    @SuppressWarnings("rawtypes")
    public boolean isStarred(Object target) {
        if(target == null) {
            return false;
        }
        StarProvider provider = this.getStarProvider(target);
        List list = this.stars.get(provider);
        for(Object item : list) {
            if(provider.equals(item, target)) {
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("rawtypes")
    public void postProcess(List targets) {
        if(targets == null || targets.isEmpty()) {
            return;
        }
        StarProvider provider = this.getStarProvider(targets.get(0));
        if(provider == null) {
            return;
        }
        
        boolean hasStar = false;
        for(Object target : targets) {
            if(this.isStarred(target)) {
                hasStar = true;
                this.starComplete(target);
            }
        }
        
        if(hasStar) {
            this.reset();
        }
    }
    
    public void postProcess(Object target) {
        this.postProcess(Arrays.asList(target));
    }
    
    public void starComplete(Object target) {
        Event event = new Event();
        event.type = EventType.StarComplete;
        event.data.put("data", target);
        EventManager.getInstance().fireEvent(event);
    }
}

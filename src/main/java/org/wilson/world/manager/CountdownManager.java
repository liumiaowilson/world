package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.countdown.CountdownTodayContentProvider;
import org.wilson.world.countdown.ICountdown;
import org.wilson.world.countdown.ICountdownProvider;

public class CountdownManager {
    private static CountdownManager instance;
    
    private List<ICountdownProvider> providers = new ArrayList<ICountdownProvider>();
    
    private CountdownManager() {
        TodayManager.getInstance().addTodayContentProvider(new CountdownTodayContentProvider());
    }
    
    public static CountdownManager getInstance() {
        if(instance == null) {
            instance = new CountdownManager();
        }
        
        return instance;
    }
    
    public void addCountdownProvider(ICountdownProvider provider) {
        if(provider != null) {
            this.providers.add(provider);
        }
    }
    
    public void removeCountdownProvider(ICountdownProvider provider) {
        if(provider != null) {
            this.providers.remove(provider);
        }
    }
    
    public List<ICountdown> getCountdowns(TimeZone tz) {
        List<ICountdown> ret = new ArrayList<ICountdown>();
        
        long now = System.currentTimeMillis();
        for(ICountdownProvider provider : this.providers) {
            List<ICountdown> countdowns = provider.getCountdowns(tz);
            if(countdowns != null) {
                for(ICountdown countdown : countdowns) {
                    if(countdown.getTarget() != null && countdown.getTarget().getTime() > now) {
                        ret.add(countdown);
                    }
                }
            }
        }
        
        return ret;
    }
}

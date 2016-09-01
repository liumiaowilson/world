package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.today.QuoteTodayContentProvider;
import org.wilson.world.today.TodayContentProvider;
import org.wilson.world.today.WordTodayContentProvider;

public class TodayManager {
    private static TodayManager instance;
    
    private List<TodayContentProvider> providers = new ArrayList<TodayContentProvider>();
    
    private TodayManager() {
        this.loadTodayContentProviders();
    }
    
    private void loadTodayContentProviders() {
        this.providers.add(new WordTodayContentProvider());
        this.providers.add(new QuoteTodayContentProvider());
    }
    
    public static TodayManager getInstance() {
        if(instance == null) {
            instance = new TodayManager();
        }
        
        return instance;
    }
    
    public void addTodayContentProvider(TodayContentProvider provider) {
        if(provider != null) {
            this.providers.add(provider);
        }
    }
    
    public void removeTodayContentProvider(TodayContentProvider provider) {
        if(provider != null) {
            this.providers.remove(provider);
        }
    }
    
    public List<TodayContentProvider> getTodayContentProviders() {
        return this.providers;
    }
}

package org.wilson.world.feed;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.FeedManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.Feed;
import org.wilson.world.web.SystemWebJob;

public class FeedJob extends SystemWebJob {
    public static final String FEED_LIST = "feed_list";
    
    private Feed feed;
    
    private int limit;
    
    public FeedJob(Feed feed) {
        this.feed = feed;
        this.setName(feed.name);
        this.setDescription(feed.description);
        
        this.limit = ConfigManager.getInstance().getConfigAsInt("feed.list.limit", 1000);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        List<FeedInfo> infos = FeedManager.getInstance().loadFeedInfo(this.feed);
        
        List<FeedInfo> old = (List<FeedInfo>) WebManager.getInstance().get(FEED_LIST);
        if(old == null) {
            old = new ArrayList<FeedInfo>();
            WebManager.getInstance().put(FEED_LIST, old);
        }
        
        for(FeedInfo info : infos) {
            while(old.size() >= limit) {
                old.remove(0);
            }
            if(!old.contains(info)) {
                old.add(info);
            }
        }
    }

}

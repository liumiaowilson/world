package org.wilson.world.feed;

public class FeedInfo {
    public String title;
    
    public String description;
    
    public String url;
    
    public boolean equals(Object target) {
        if(target instanceof FeedInfo) {
            FeedInfo info = (FeedInfo)target;
            if(this.title != null) {
                return this.title.equals(info.title);
            }
        }
        
        return false;
    }
}

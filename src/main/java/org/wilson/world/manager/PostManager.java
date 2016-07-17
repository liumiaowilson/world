package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

public class PostManager {
    private static PostManager instance;
    
    private List<String> postbox = new ArrayList<String>();
    private int lengthLimit, sizeLimit;
    
    private PostManager() {
        this.lengthLimit = ConfigManager.getInstance().getConfigAsInt("post.length.limit", 50);
        this.sizeLimit = ConfigManager.getInstance().getConfigAsInt("post.size.limit", 50);
    }
    
    public static PostManager getInstance() {
        if(instance == null) {
            instance = new PostManager();
        }
        return instance;
    }
    
    public void addPost(String post) {
        if(post == null) {
            return;
        }
        if(post.length() > this.lengthLimit) {
            //rejected
            return;
        }
        if(this.postbox.size() > this.sizeLimit) {
            //rejected
            return;
        }
        if(!this.postbox.contains(post)) {
            this.postbox.add(post);
        }
    }
    
    public List<String> getPosts() {
        return this.postbox;
    }
    
    public void clear() {
        this.postbox.clear();
    }
}

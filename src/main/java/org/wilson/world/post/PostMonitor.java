package org.wilson.world.post;

import java.util.List;

import org.wilson.world.manager.PostManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;

public class PostMonitor implements MonitorParticipant {
    private Alert alert;
    
    public PostMonitor() {
        this.alert = new Alert();
        this.alert.id = "Posts detected";
        this.alert.message = "There have been incoming posts. Please go and process them.";
        this.alert.url = "post_process.jsp";
    }
    
    @Override
    public boolean isOK() {
        List<String> posts = PostManager.getInstance().getPosts();
        if(posts.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}

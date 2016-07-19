package org.wilson.world.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.post.PostMonitor;

public class PostManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(PostManager.class);
    
    public static final String SAVED_POSTS = "saved_posts";
    
    private static PostManager instance;
    
    private List<String> postbox = new ArrayList<String>();
    private int lengthLimit, sizeLimit;
    private PostMonitor monitor = new PostMonitor();
    
    private PostManager() {
        this.lengthLimit = ConfigManager.getInstance().getConfigAsInt("post.length.limit", 50);
        this.sizeLimit = ConfigManager.getInstance().getConfigAsInt("post.size.limit", 50);
        
        MonitorManager.getInstance().registerMonitorParticipant(monitor);
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
    
    public void removeAlert() {
        MonitorManager.getInstance().removeAlert(this.monitor.getAlert());
    }

    @Override
    public void start() {
        File savedPostsFile = new File(ConfigManager.getInstance().getDataDir() + SAVED_POSTS);
        if(savedPostsFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(savedPostsFile));
                String line = null;
                while((line = br.readLine()) != null) {
                    this.postbox.add(line);
                }
            }
            catch(Exception e) {
                logger.error(e);
            }
            finally {
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
                }
            }
            
            savedPostsFile.delete();
        }
    }

    @Override
    public void shutdown() {
        if(!this.postbox.isEmpty()) {
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(ConfigManager.getInstance().getDataDir() + SAVED_POSTS);
                for(String post : this.postbox) {
                    pw.println(post);
                }
                pw.flush();
            }
            catch(Exception e) {
                logger.error(e);
            }
            finally {
                if(pw != null) {
                    pw.close();
                }
            }
        }
    }
}

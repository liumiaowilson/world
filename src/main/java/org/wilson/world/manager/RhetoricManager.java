package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.RhetoricalDevice;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RhetoricManager {
    public static final String NAME = "rhetorical_device";
    
    private static RhetoricManager instance;
    
    private DAO<RhetoricalDevice> dao = null;
    
    @SuppressWarnings("unchecked")
    private RhetoricManager() {
        this.dao = DAOManager.getInstance().getDAO(RhetoricalDevice.class);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return NAME;
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(RhetoricalDevice device : getRhetoricalDevices()) {
                    boolean found = device.name.contains(text) || device.definition.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = device.id;
                        content.name = device.name;
                        content.description = device.definition;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RhetoricManager getInstance() {
        if(instance == null) {
            instance = new RhetoricManager();
        }
        return instance;
    }
    
    public void createRhetoricalDevice(RhetoricalDevice device) {
    }
    
    public RhetoricalDevice getRhetoricalDevice(int id) {
        RhetoricalDevice device = this.dao.get(id);
        if(device != null) {
            return device;
        }
        else {
            return null;
        }
    }
    
    public List<RhetoricalDevice> getRhetoricalDevices() {
        List<RhetoricalDevice> result = new ArrayList<RhetoricalDevice>();
        for(RhetoricalDevice device : this.dao.getAll()) {
            result.add(device);
        }
        return result;
    }
    
    public void updateRhetoricalDevice(RhetoricalDevice device) {
    }
    
    public void deleteRhetoricalDevice(int id) {
    }
}

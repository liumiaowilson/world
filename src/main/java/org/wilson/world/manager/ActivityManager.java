package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.activity.ActivityIdeaConverter;
import org.wilson.world.dao.DAO;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Activity;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ActivityManager implements ItemTypeProvider {
    public static final String NAME = "activity";
    
    private static ActivityManager instance;
    
    private DAO<Activity> dao = null;
    
    @SuppressWarnings("unchecked")
    private ActivityManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Activity.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        IdeaConverterFactory.getInstance().addIdeaConverter(new ActivityIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Activity activity : getActivities()) {
                    boolean found = activity.name.contains(text) || activity.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = activity.id;
                        content.name = activity.name;
                        content.description = activity.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ActivityManager getInstance() {
        if(instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }
    
    public void createActivity(Activity activity) {
        ItemManager.getInstance().checkDuplicate(activity);
        
        this.dao.create(activity);
    }
    
    public Activity getActivity(int id) {
    	Activity activity = this.dao.get(id);
        if(activity != null) {
            return activity;
        }
        else {
            return null;
        }
    }
    
    public List<Activity> getActivities() {
        List<Activity> result = new ArrayList<Activity>();
        for(Activity activity : this.dao.getAll()) {
            result.add(activity);
        }
        return result;
    }
    
    public void updateActivity(Activity activity) {
        this.dao.update(activity);
    }
    
    public void deleteActivity(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Activity;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Activity activity = (Activity)target;
        return String.valueOf(activity.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Activity activity = (Activity)target;
        return activity.name;
    }
    
    public Activity randomActivity() {
    	List<Activity> activities = this.getActivities();
    	if(activities.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(activities.size());
    	return activities.get(n);
    }
}

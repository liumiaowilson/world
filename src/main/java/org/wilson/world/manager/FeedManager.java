package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Feed;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class FeedManager implements ItemTypeProvider {
    public static final String NAME = "feed";
    
    private static FeedManager instance;
    
    private DAO<Feed> dao = null;
    
    @SuppressWarnings("unchecked")
    private FeedManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Feed.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Feed feed : getFeeds()) {
                    boolean found = feed.name.contains(text) || feed.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = feed.id;
                        content.name = feed.name;
                        content.description = feed.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FeedManager getInstance() {
        if(instance == null) {
            instance = new FeedManager();
        }
        return instance;
    }
    
    public void createFeed(Feed feed) {
        ItemManager.getInstance().checkDuplicate(feed);
        
        this.dao.create(feed);
    }
    
    public Feed getFeed(int id) {
        Feed feed = this.dao.get(id);
        if(feed != null) {
            return feed;
        }
        else {
            return null;
        }
    }
    
    public List<Feed> getFeeds() {
        List<Feed> result = new ArrayList<Feed>();
        for(Feed feed : this.dao.getAll()) {
            result.add(feed);
        }
        return result;
    }
    
    public void updateFeed(Feed feed) {
        this.dao.update(feed);
    }
    
    public void deleteFeed(int id) {
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
        return target instanceof Feed;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Feed feed = (Feed)target;
        return String.valueOf(feed.id);
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
        
        Feed feed = (Feed)target;
        return feed.name;
    }
}

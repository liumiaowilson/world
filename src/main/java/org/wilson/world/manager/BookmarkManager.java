package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Bookmark;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class BookmarkManager implements ItemTypeProvider {
    public static final String NAME = "bookmark";
    
    private static BookmarkManager instance;
    
    private DAO<Bookmark> dao = null;
    
    private Map<String, List<Bookmark>> bookmarks = null;
    
    @SuppressWarnings("unchecked")
    private BookmarkManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Bookmark.class);
        this.bookmarks = new HashMap<String, List<Bookmark>>();
        ((CachedDAO<Bookmark>)this.dao).getCache().addCacheListener(new CacheListener<Bookmark>(){

			@Override
			public void cachePut(Bookmark old, Bookmark v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				List<Bookmark> list = bookmarks.get(v.group);
				if(list == null) {
					list = new ArrayList<Bookmark>();
					bookmarks.put(v.group, list);
				}
				if(!list.contains(v)) {
					list.add(v);
				}
			}

			@Override
			public void cacheDeleted(Bookmark v) {
				List<Bookmark> list = bookmarks.get(v);
				if(list != null) {
					list.remove(v);
				}
			}

			@Override
			public void cacheLoaded(List<Bookmark> all) {
			}

			@Override
			public void cacheLoading(List<Bookmark> old) {
				BookmarkManager.this.bookmarks.clear();
			}
        	
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Bookmark bookmark : getBookmarks()) {
                    boolean found = bookmark.name.contains(text) || bookmark.content.contains(text) || bookmark.url.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = bookmark.id;
                        content.name = bookmark.name;
                        content.description = bookmark.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static BookmarkManager getInstance() {
        if(instance == null) {
            instance = new BookmarkManager();
        }
        return instance;
    }
    
    public void createBookmark(Bookmark bookmark) {
        ItemManager.getInstance().checkDuplicate(bookmark);
        
        this.dao.create(bookmark);
    }
    
    public Bookmark getBookmark(int id) {
    	Bookmark bookmark = this.dao.get(id);
        if(bookmark != null) {
            return bookmark;
        }
        else {
            return null;
        }
    }
    
    public List<Bookmark> getBookmarks() {
        List<Bookmark> result = new ArrayList<Bookmark>();
        for(Bookmark bookmark : this.dao.getAll()) {
            result.add(bookmark);
        }
        return result;
    }
    
    public void updateBookmark(Bookmark bookmark) {
        this.dao.update(bookmark);
    }
    
    public void deleteBookmark(int id) {
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
        return target instanceof Bookmark;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Bookmark bookmark = (Bookmark)target;
        return String.valueOf(bookmark.id);
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
        
        Bookmark bookmark = (Bookmark)target;
        return bookmark.name;
    }
    
    public List<String> getBookmarkGroups() {
    	return new ArrayList<String>(this.bookmarks.keySet());
    }
    
    public List<Bookmark> getBookmarksByGroup(String group) {
    	if(StringUtils.isBlank(group)) {
    		return Collections.emptyList();
    	}
    	
    	return bookmarks.get(group);
    }
}

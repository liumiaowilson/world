package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.link.LinkDBCleaner;
import org.wilson.world.model.Link;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class LinkManager implements ItemTypeProvider, ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(LinkManager.class);
    
    public static final String NAME = "link";
    
    private static LinkManager instance;
    
    private DAO<Link> dao = null;
    
    private List<String> supportedItemTypes = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private LinkManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Link.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new LinkDBCleaner());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Link link : getLinks()) {
                    boolean found = link.name.contains(text) || link.label.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = link.id;
                        content.name = link.name;
                        content.description = link.label;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static LinkManager getInstance() {
        if(instance == null) {
            instance = new LinkManager();
        }
        return instance;
    }
    
    private void loadSupportedItemTypes() {
        String str = ConfigManager.getInstance().getConfig("link.item_type.supported");
        if(!StringUtils.isBlank(str)) {
            for(String itemType : str.trim().split(",")) {
                itemType = itemType.trim();
                if(ItemManager.getInstance().isSupportedItemType(itemType)) {
                    this.supportedItemTypes.add(itemType);
                }
            }
        }
    }
    
    public List<String> getSupportedItemTypes() {
        return this.supportedItemTypes;
    }
    
    public void createLink(Link link) {
        ItemManager.getInstance().checkDuplicate(link);
        
        this.dao.create(link);
    }
    
    public Link getLink(int id) {
        Link link = this.dao.get(id);
        if(link != null) {
            return link;
        }
        else {
            return null;
        }
    }
    
    public List<Link> getLinks() {
        List<Link> result = new ArrayList<Link>();
        for(Link link : this.dao.getAll()) {
            result.add(link);
        }
        return result;
    }
    
    public List<Link> getLinks(String itemType, int itemId) {
    	List<Link> links = new ArrayList<Link>();
    	
    	for(Link link : this.getLinks()) {
    		if(link.itemType.equals(itemType) && link.itemId == itemId) {
    			links.add(link);
    		}
    	}
    	
    	return links;
    }
    
    public void updateLink(Link link) {
        this.dao.update(link);
    }
    
    public void deleteLink(int id) {
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
        return target instanceof Link;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Link link = (Link)target;
        return String.valueOf(link.id);
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
        
        Link link = (Link)target;
        return link.name;
    }

    @Override
    public void start() {
        logger.info("Start to load supported item types for linking");
        this.loadSupportedItemTypes();
    }

    @Override
    public void shutdown() {
    }
    
    public boolean isSupportedItemType(String itemType) {
        if(StringUtils.isBlank(itemType)) {
            return false;
        }
        
        return this.supportedItemTypes.contains(itemType);
    }
    
    public boolean isSupported(Object target) {
        if(target == null) {
            return false;
        }
        
        String itemType = ItemManager.getInstance().getItemTypeName(target);
        return this.isSupportedItemType(itemType);
    }
    
    public List<Link> getLinks(Object target) {
        if(this.isSupported(target)) {
            try {
                ItemTypeProvider provider = ItemManager.getInstance().getItemTypeProvider(target);
                if(provider != null) {
                    int id = Integer.parseInt(provider.getID(target));
                    List<Link> ret = new ArrayList<Link>();
                    for(Link link : this.getLinks()) {
                        if(link.itemType.equals(provider.getItemTypeName()) && link.itemId == id) {
                            ret.add(link);
                        }
                    }
                    
                    return ret;
                }
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
        
        return Collections.emptyList();
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Proxy;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ProxyManager implements ItemTypeProvider {
    public static final String NAME = "proxy";
    
    private static ProxyManager instance;
    
    private DAO<Proxy> dao = null;
    
    @SuppressWarnings("unchecked")
    private ProxyManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Proxy.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Proxy proxy : getProxies()) {
                    boolean found = proxy.name.contains(text) || proxy.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = proxy.id;
                        content.name = proxy.name;
                        content.description = proxy.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static ProxyManager getInstance() {
        if(instance == null) {
            instance = new ProxyManager();
        }
        return instance;
    }
    
    public void createProxy(Proxy proxy) {
        ItemManager.getInstance().checkDuplicate(proxy);
        
        this.dao.create(proxy);
    }
    
    public Proxy getProxy(int id) {
        Proxy proxy = this.dao.get(id);
        if(proxy != null) {
            return proxy;
        }
        else {
            return null;
        }
    }
    
    public List<Proxy> getProxies() {
        List<Proxy> result = new ArrayList<Proxy>();
        for(Proxy proxy : this.dao.getAll()) {
            result.add(proxy);
        }
        return result;
    }
    
    public void updateProxy(Proxy proxy) {
        this.dao.update(proxy);
    }
    
    public void deleteProxy(int id) {
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
        return target instanceof Proxy;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Proxy proxy = (Proxy)target;
        return String.valueOf(proxy.id);
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
        
        Proxy proxy = (Proxy)target;
        return proxy.name;
    }
    
    public void setWebProxyUrl(String url) {
    	DataManager.getInstance().setValue("web_proxy.url", url);
    }
    
    public String getWebProxyUrl() {
    	return DataManager.getInstance().getValue("web_proxy.url");
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.model.Proxy;
import org.wilson.world.proxy.AbstractDynamicProxyProvider;
import org.wilson.world.proxy.DefaultDynamicProxyProvider;
import org.wilson.world.proxy.DynamicProxy;
import org.wilson.world.proxy.DynamicProxyProvider;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ProxyManager implements ItemTypeProvider, JavaExtensionListener<AbstractDynamicProxyProvider> {
    public static final String NAME = "proxy";
    
    private static ProxyManager instance;
    
    private DAO<Proxy> dao = null;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, DynamicProxyProvider> providers = new HashMap<Integer, DynamicProxyProvider>();
    
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
        
        ExtManager.getInstance().addJavaExtensionListener(this);
        
        this.loadSystemDynamicProxyProviders();
    }
    
    private void loadSystemDynamicProxyProviders() {
    	this.addDynamicProxyProvider(DefaultDynamicProxyProvider.getInstance());
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

	@Override
	public Class<AbstractDynamicProxyProvider> getExtensionClass() {
		return AbstractDynamicProxyProvider.class;
	}

	@Override
	public void created(AbstractDynamicProxyProvider t) {
		this.addDynamicProxyProvider(t);
	}

	@Override
	public void removed(AbstractDynamicProxyProvider t) {
		this.removeDynamicProxyProvider(t);
	}
	
	public void addDynamicProxyProvider(DynamicProxyProvider provider) {
		if(provider != null) {
			provider.setId(GLOBAL_ID++);
			this.providers.put(provider.getId(), provider);
		}
	}
	
	public void removeDynamicProxyProvider(DynamicProxyProvider provider) {
		if(provider != null) {
			this.providers.remove(provider.getId());
		}
	}
	
	public List<DynamicProxyProvider> getDynamicProxyProviders() {
		return new ArrayList<DynamicProxyProvider>(this.providers.values());
	}
	
	public DynamicProxyProvider getDynamicProxyProvider(int id) {
		return this.providers.get(id);
	}
	
	public DynamicProxyProvider getDynamicProxyProvider(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(DynamicProxyProvider provider : this.providers.values()) {
			if(name.equals(provider.getName())) {
				return provider;
			}
		}
		
		return null;
	}
	
	public List<DynamicProxy> getDynamicProxies() {
		List<DynamicProxy> proxies = new ArrayList<DynamicProxy>();
		for(DynamicProxyProvider provider : this.providers.values()) {
			List<DynamicProxy> ps = provider.getProxies();
			if(ps != null && !ps.isEmpty()) {
				proxies.addAll(ps);
			}
		}
		
		return proxies;
	}
	
	public DynamicProxy randomDynamicProxy() {
		List<DynamicProxy> proxies = this.getDynamicProxies();
		if(proxies.isEmpty()) {
			return null;
		}
		
		int n = DiceManager.getInstance().random(proxies.size());
		return proxies.get(n);
	}
}

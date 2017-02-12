package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Rewrite;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RewriteManager implements ItemTypeProvider {
    public static final String NAME = "rewrite";
    
    private static RewriteManager instance;
    
    private DAO<Rewrite> dao = null;
    
    @SuppressWarnings("unchecked")
    private RewriteManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Rewrite.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Rewrite rewrite : getRewrites()) {
                    boolean found = rewrite.name.contains(text) || rewrite.fromUrl.contains(text) || rewrite.toUrl.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = rewrite.id;
                        content.name = rewrite.name;
                        content.description = rewrite.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RewriteManager getInstance() {
        if(instance == null) {
            instance = new RewriteManager();
        }
        return instance;
    }
    
    public void createRewrite(Rewrite rewrite) {
        ItemManager.getInstance().checkDuplicate(rewrite);
        
        this.dao.create(rewrite);
    }
    
    public Rewrite getRewrite(int id) {
    	Rewrite rewrite = this.dao.get(id);
        if(rewrite != null) {
            return rewrite;
        }
        else {
            return null;
        }
    }
    
    public List<Rewrite> getRewrites() {
        List<Rewrite> result = new ArrayList<Rewrite>();
        for(Rewrite rewrite : this.dao.getAll()) {
            result.add(rewrite);
        }
        return result;
    }
    
    public void updateRewrite(Rewrite rewrite) {
        this.dao.update(rewrite);
    }
    
    public void deleteRewrite(int id) {
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
        return target instanceof Rewrite;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Rewrite rewrite = (Rewrite)target;
        return String.valueOf(rewrite.id);
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
        
        Rewrite rewrite = (Rewrite)target;
        return rewrite.name;
    }
    
    public String rewrite(Rewrite rewrite, String url) {
    	if(rewrite == null || StringUtils.isBlank(url)) {
    		return null;
    	}
    	
    	if("true".equals(rewrite.regex)) {
    		if(url.matches(rewrite.fromUrl)) {
    			return url.replaceAll(rewrite.fromUrl, rewrite.toUrl);
    		}
    		else {
    			return null;
    		}
    	}
    	else {
    		return url.equals(rewrite.fromUrl) ? rewrite.toUrl : null;
    	}
    }
    
    public String rewrite(String url) {
    	if(StringUtils.isBlank(url)) {
    		return null;
    	}
    	
    	for(Rewrite rewrite : this.getRewrites()) {
    		String ret = this.rewrite(rewrite, url);
    		if(ret != null) {
    			return ret;
    		}
    	}
    	
    	return null;
    }
}

package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Pagelet;
import org.wilson.world.pagelet.Page;
import org.wilson.world.pagelet.PageletJumpPageMenuItemProvider;
import org.wilson.world.pagelet.PageletType;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class PageletManager implements ItemTypeProvider {
    public static final String NAME = "pagelet";
    
    private static PageletManager instance;
    
    private DAO<Pagelet> dao = null;
    
    private List<String> types = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private PageletManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Pagelet.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Pagelet pagelet : getPagelets()) {
                    boolean found = pagelet.name.contains(text) || pagelet.title.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = pagelet.id;
                        content.name = pagelet.name;
                        content.description = pagelet.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.loadTypes();
        
        MenuManager.getInstance().addJumpPageMenuItemProvider(new PageletJumpPageMenuItemProvider());
    }
    
    private void loadTypes() {
    	for(PageletType type : PageletType.values()) {
    		this.types.add(type.name());
    	}
    }
    
    public static PageletManager getInstance() {
        if(instance == null) {
            instance = new PageletManager();
        }
        return instance;
    }
    
    public void createPagelet(Pagelet pagelet) {
        ItemManager.getInstance().checkDuplicate(pagelet);
        
        this.dao.create(pagelet);
    }
    
    public Pagelet getPagelet(int id, boolean lazy) {
    	Pagelet pagelet = this.dao.get(id, lazy);
        if(pagelet != null) {
            return pagelet;
        }
        else {
            return null;
        }
    }
    
    public Pagelet getPagelet(int id) {
    	return getPagelet(id, true);
    }
    
    public Pagelet getPagelet(String name, boolean lazy) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(Pagelet pagelet : this.getPagelets()) {
    		if(pagelet.name.equals(name)) {
    			return lazy ? pagelet : this.load(pagelet);
    		}
    	}
    	
    	return null;
    }
    
    public Pagelet getPagelet(String name) {
    	return this.getPagelet(name, true);
    }
    
    public List<Pagelet> getPagelets() {
        List<Pagelet> result = new ArrayList<Pagelet>();
        for(Pagelet pagelet : this.dao.getAll()) {
            result.add(pagelet);
        }
        return result;
    }
    
    public void updatePagelet(Pagelet pagelet) {
        this.dao.update(pagelet);
    }
    
    public void deletePagelet(int id) {
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
        return target instanceof Pagelet;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Pagelet pagelet = (Pagelet)target;
        return String.valueOf(pagelet.id);
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
        
        Pagelet pagelet = (Pagelet)target;
        return pagelet.name;
    }
    
    public Page executeServerCode(Pagelet pagelet, HttpServletRequest req, HttpServletResponse resp) {
    	Page page = new Page();
    	
    	if(pagelet == null) {
    		return page;
    	}
    	
    	if(StringUtils.isBlank(pagelet.serverCode)) {
    		return page;
    	}
    	
    	Map<String, Object> context = new HashMap<String, Object>();
    	context.put("request", req);
    	context.put("response", resp);
    	context.put("page", page);
    	
    	String setup =  "function set(name, value) {\n"
    				+	"    if(value instanceof String) {\n"
    				+	"        page.set(name, '\"' + value + '\"');\n"
    				+	"    }\n"
    				+	"    else {\n"
    				+	"        page.set(name, JSON.stringify(value));\n"
    				+	"    }\n"
    				+	"}\n"
    				+	"\n"
    				+	"function get(name) {\n"
    				+	"    var val = page.get(name);\n"
    				+	"    return val ? JSON.parse(val) : val\n"
    				+	"}\n"
    				+	"\n";
    	
    	Object ret = ScriptManager.getInstance().run(setup + pagelet.serverCode, context);
    	if(ret instanceof String) {
    		page.setNext((String) ret);
    	}
    	
    	return page;
    }
    
    public List<Pagelet> getMatchingPagelets(String url) {
    	List<Pagelet> ret = new ArrayList<Pagelet>();
    	
    	if(StringUtils.isBlank(url)) {
    		return ret;
    	}
    	
    	for(Pagelet pagelet : this.getPagelets()) {
    		if(StringUtils.isNotBlank(pagelet.target)) {
    			if(url.matches(pagelet.target)) {
    				ret.add(pagelet);
    			}
    		}
    	}
    	
    	return ret;
    }
    
    public Pagelet load(Pagelet pagelet) {
    	if(pagelet == null) {
    		return null;
    	}
    	
    	return this.getPagelet(pagelet.id, false);
    }
    
    public List<String> getPageletTypes() {
    	return Collections.unmodifiableList(this.types);
    }
    
    public List<Pagelet> getPagelets(PageletType type) {
    	List<Pagelet> ret = new ArrayList<Pagelet>();
    	
    	for(Pagelet pagelet : this.getPagelets()) {
    		if(type == null || StringUtils.isBlank(pagelet.type) || type.name().equals(pagelet.type)) {
    			ret.add(pagelet);
    		}
    	}
    	
    	return ret;
    }
}

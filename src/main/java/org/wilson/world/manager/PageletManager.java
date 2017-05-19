package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.image.ImageRef;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Pagelet;
import org.wilson.world.pagelet.FieldInfo;
import org.wilson.world.pagelet.Page;
import org.wilson.world.pagelet.PageletJumpPageMenuItemProvider;
import org.wilson.world.pagelet.PageletType;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PageletManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(PageletManager.class);
	
    public static final String NAME = "pagelet";
    
    private static PageletManager instance;
    
    private DAO<Pagelet> dao = null;

    private Map<Integer, Pagelet> cachedPagelets = new HashMap<Integer, Pagelet>();
    private Map<Integer, Pagelet> backupPagelets = new HashMap<Integer, Pagelet>();
    
    private List<String> types = new ArrayList<String>();
    
    private List<String> resources = new ArrayList<String>();
    
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

    public Map<Integer, Pagelet> getCachedPagelets() {
        return this.cachedPagelets;
    }

    public Map<Integer, Pagelet> getBackupPagelets() {
        return this.backupPagelets;
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

    public Pagelet clonePagelet(Pagelet pagelet) {
        if(pagelet == null) return null;

        Pagelet cloned = new Pagelet();
        cloned.id = pagelet.id;
        cloned.name = pagelet.name;
        cloned.title = pagelet.title;
        cloned.target = pagelet.target;
        cloned.type = pagelet.type;
        cloned.serverCode = pagelet.serverCode;
        cloned.css = pagelet.css;
        cloned.html = pagelet.html;
        cloned.clientCode = pagelet.clientCode;

        return cloned;
    }
    
    public Pagelet getPagelet(int id, boolean lazy) {
        Pagelet pagelet = null;
        if(lazy) {
            pagelet = this.dao.get(id, lazy);
            return pagelet;
        }
        else {
            pagelet = this.cachedPagelets.get(id);
            if(pagelet == null) {
                pagelet = this.dao.get(id, lazy);
                pagelet = clonePagelet(pagelet);
                this.cachedPagelets.put(id, pagelet);
            }

            return clonePagelet(pagelet);
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
        if(pagelet == null) {
            return;
        }

        Pagelet oldPagelet = this.cachedPagelets.remove(pagelet.id);
        if(oldPagelet != null) {
            this.backupPagelets.put(oldPagelet.id, oldPagelet);
        }

        this.dao.update(pagelet);
        this.cachedPagelets.put(pagelet.id, pagelet);
    }
    
    public void deletePagelet(int id) {
        Pagelet oldPagelet = this.cachedPagelets.remove(id);
        if(oldPagelet != null) {
            this.backupPagelets.put(oldPagelet.id, oldPagelet);
        }

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
    	return this.executeServerCode(pagelet, req, resp, null);
    }
    
    public Page executeServerCode(Pagelet pagelet, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> data) {
    	Page page = new Page();
    	
    	if(pagelet == null) {
    		return page;
    	}
    	
    	page.setCss(pagelet.css);
    	page.setHtml(pagelet.html);
    	page.setClientCode(pagelet.clientCode);

        if("Public".equals(pagelet.type)) {
            page.addMeta("viewport", "width=device-width, initial-scale=1");

            ConfigManager cm = ConfigManager.getInstance();
            page.addStyle(cm.getConfig("css.bootstrap.url", "../css/bootstrap.min.css"));

            page.addScript(cm.getConfig("js.jquery.url", "../js/jquery-2.2.4.min.js"));
            page.addScript(cm.getConfig("js.bootstrap.url", "../js/bootstrap.min.js"));
        }
    	
    	if(StringUtils.isBlank(pagelet.serverCode)) {
    		return page;
    	}
    	
    	Map<String, Object> context = new HashMap<String, Object>();
    	if(data != null) {
    		context.putAll(data);
    	}
    	
    	context.put("request", req);
    	context.put("response", resp);
    	context.put("page", page);
    	
    	String setup =  "function set(name, value) {\n"
    				+	"    if(!value) return;\n"
    				+	"    if(!value.constructor) {\n"
    				+	"        page.set(name, value);\n"
    				+	"    }\n"
    				+	"    else if(value.hasOwnProperty('equals')) {\n"
    				+	"        page.set(name, JSON.stringify(String(value)));\n"
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
    	
    	String back = page.getBack();
    	if(StringUtils.isNotBlank(back)) {
    		URLManager.getInstance().setLastUrl(URLManager.getInstance().getBaseUrl() + back);
    	}
    	
    	Map<String, Object> variables = page.getVariables();
    	if(!variables.isEmpty()) {
    		String css = page.getCss();
        	if(StringUtils.isNotBlank(css)) {
        		css = TemplateManager.getInstance().evaluate(css, variables);
        		page.setCss(css);
        	}
        	
        	String html = page.getHtml();
        	if(StringUtils.isNotBlank(html)) {
        		html = TemplateManager.getInstance().evaluate(html, variables);
        		page.setHtml(html);
        	}
        	
        	String clientCode = page.getClientCode();
        	if(StringUtils.isNotBlank(clientCode)) {
        		clientCode = TemplateManager.getInstance().evaluate(clientCode, variables);
        		page.setClientCode(clientCode);
        	}
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
    
    public List<FieldInfo> parseFieldInfos(String json) {
    	List<FieldInfo> ret = new ArrayList<FieldInfo>();
    	if(StringUtils.isBlank(json)) {
			return ret;
		}
		
		try {
			JSONArray array = JSONArray.fromObject(json);
			for(int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				FieldInfo info = new FieldInfo();
				if(!(obj.containsKey("name") && obj.containsKey("label") && obj.containsKey("type"))) {
					continue;
				}
				info.name = obj.getString("name");
				info.label = obj.getString("label");
				info.type = obj.getString("type");
				if(obj.containsKey("data")) {
					JSONObject data = obj.getJSONObject("data");
					for(Object keyObj : data.keySet()) {
						String key = (String) keyObj;
						Object value = JSONUtils.convert(data.get(key));
						info.data.put(key, value);
					}
				}
				
				ret.add(info);
			}
		}
		catch(Exception e) {
			logger.error(e);
		}
		
		return ret;
    }
    
    /**
     * Add a resource that can be referred by a pagelet
     * 
     * @param resource
     */
    public void addResource(String resource) {
    	if(StringUtils.isBlank(resource)) {
    		return;
    	}
    	
    	if(!this.resources.contains(resource)) {
    		this.resources.add(resource);
    	}
    }
    
    public void removeResource(String resource) {
    	this.resources.remove(resource);
    }
    
    public List<String> getResources() {
    	return this.resources;
    }
    
    public boolean canVisit(ImageRef ref) {
    	if(ref == null) {
    		return false;
    	}
    	
    	String name = ref.getName();
    	for(String resource : resources) {
    		if(name.matches(resource)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}

package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.controller.ControllerJumpPageMenuItemProvider;
import org.wilson.world.form.FormJumpPageMenuItemProvider;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.menu.ActiveMenu;
import org.wilson.world.menu.ActiveToolbar;
import org.wilson.world.menu.JumpPageMenuItemProvider;
import org.wilson.world.menu.MenuInfo;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemProvider;
import org.wilson.world.menu.MenuItemRole;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MenuManager implements JavaExtensionListener<JumpPageMenuItemProvider>, ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(MenuManager.class);
    
    private static MenuManager instance;
    
    private List<MenuItem> menus = new ArrayList<MenuItem>();
    private Map<String, MenuItem> map = new HashMap<String, MenuItem>();
    
    private List<MenuItem> toolbar = new ArrayList<MenuItem>();
    
    private Map<String, JumpPageMenuItemProvider> providers = new HashMap<String, JumpPageMenuItemProvider>();
    private Map<String, ActiveMenu> activeMenus = new HashMap<String, ActiveMenu>();
    private Map<String, ActiveToolbar> activeToolbars = new HashMap<String, ActiveToolbar>();
    
    private MenuManager() {
        this.loadSystemJumpPageMenuItemProviders();
        
        ExtManager.getInstance().addJavaExtensionListener(this);
        
        ExtManager.getInstance().addJavaExtensionListener(new JavaExtensionListener<ActiveMenu>() {

			@Override
			public Class<ActiveMenu> getExtensionClass() {
				return ActiveMenu.class;
			}

			@Override
			public void created(ActiveMenu t) {
				addActiveMenu(t);
				
				loadMenus();
			}

			@Override
			public void removed(ActiveMenu t) {
				removeActiveMenu(t);
				
				loadMenus();
			}
        	
        });
        
        ExtManager.getInstance().addJavaExtensionListener(new JavaExtensionListener<ActiveToolbar>() {

			@Override
			public Class<ActiveToolbar> getExtensionClass() {
				return ActiveToolbar.class;
			}

			@Override
			public void created(ActiveToolbar t) {
				addActiveToolbar(t);
				
				loadToolbar();
			}

			@Override
			public void removed(ActiveToolbar t) {
				removeActiveToolbar(t);
				
				loadToolbar();
			}
        	
        });
    }
    
    public static MenuManager getInstance() {
        if(instance == null) {
            instance = new MenuManager();
        }
        
        return instance;
    }
    
    public void addActiveMenu(ActiveMenu menu) {
    	if(menu != null && menu.getName() != null) {
    		this.activeMenus.put(menu.getName(), menu);
    	}
    }
    
    public void removeActiveMenu(ActiveMenu menu) {
    	if(menu != null && menu.getName() != null) {
    		this.activeMenus.remove(menu.getName());
    	}
    }
    
    public List<ActiveMenu> getActiveMenus() {
    	return new ArrayList<ActiveMenu>(this.activeMenus.values());
    }
    
    public void addActiveToolbar(ActiveToolbar toolbar) {
    	if(toolbar != null && toolbar.getName() != null) {
    		this.activeToolbars.put(toolbar.getName(), toolbar);
    	}
    }
    
    public void removeActiveToolbar(ActiveToolbar toolbar) {
    	if(toolbar != null && toolbar.getName() != null) {
    		this.activeToolbars.remove(toolbar.getName());
    	}
    }
    
    public List<ActiveToolbar> getActiveToolbars() {
    	return new ArrayList<ActiveToolbar>(this.activeToolbars.values());
    }
    
    public MenuItem getNavbarMenuItem(String id) {
    	if(StringUtils.isBlank(id)) {
    		return null;
    	}
    	
    	return this.map.get(id);
    }
    
    public MenuItem getToolbarMenuItem(String id) {
    	if(StringUtils.isBlank(id)) {
    		return null;
    	}
    	
    	for(MenuItem item : this.toolbar) {
    		if(id.equals(item.id)) {
    			return item;
    		}
    	}
    	
    	return null;
    }
    
    public void addNavbarMenuItem(MenuItem item) {
    	if(item == null) {
    		return;
    	}
    	
    	if(StringUtils.isBlank(item.id)) {
    		return;
    	}
    	
    	MenuItem parent = item.parent;
    	if(parent == null) {
    		return;
    	}
    	
    	if(!parent.menus.contains(item)) {
    		parent.menus.add(item);
    	}
    	
    	this.map.put(item.id, item);
    }
    
    public void addToolbarMenuItem(MenuItem item) {
    	if(item == null) {
    		return;
    	}
    	
    	if(StringUtils.isBlank(item.id)) {
    		return;
    	}
    	
    	if(!this.toolbar.contains(item)) {
    		this.toolbar.add(item);
    	}
    }
    
    public MenuItem removeNavbarMenuItem(String id) {
    	if(StringUtils.isBlank(id)) {
    		return null;
    	}
    	
    	MenuItem item = this.getNavbarMenuItem(id);
    	if(item == null) {
    		return null;
    	}
    	
    	this.map.remove(item.id);
    	
    	MenuItem parent = item.parent;
    	if(parent == null) {
    		return item;
    	}
    	
    	parent.menus.remove(item);
    	
    	return item;
    }
    
    public MenuItem removeToolbarMenuItem(String id) {
    	if(StringUtils.isBlank(id)) {
    		return null;
    	}
    	
    	MenuItem item = this.getToolbarMenuItem(id);
    	if(item == null) {
    		return null;
    	}
    	
    	this.toolbar.remove(item);
    	
    	return item;
    }
    
    public List<JumpPageMenuItemProvider> getJumpPageMenuItemProviders() {
    	return new ArrayList<JumpPageMenuItemProvider>(this.providers.values());
    }
    
    public Map<String, MenuItem> getJumpPageExtMenuItems() {
    	Map<String, MenuItem> ret = new HashMap<String, MenuItem>();
    	for(JumpPageMenuItemProvider provider : this.providers.values()) {
    		Map<String, MenuItem> items = provider.getSingleMenuItems();
    		if(items != null) {
    			for(Entry<String, MenuItem> entry : items.entrySet()) {
    				String id = entry.getKey();
    				MenuItem item = entry.getValue();
    				id = provider.getName() + "_" + id;
    				ret.put(id, item);
    			}
    		}
    	}
    	
    	return ret;
    }
    
    private void loadSystemJumpPageMenuItemProviders() {
    	this.addJumpPageMenuItemProvider(new ControllerJumpPageMenuItemProvider());
    	this.addJumpPageMenuItemProvider(new FormJumpPageMenuItemProvider());
    }
    
    public void addJumpPageMenuItemProvider(JumpPageMenuItemProvider provider) {
    	if(provider != null && provider.getName() != null) {
    		this.providers.put(provider.getName(), provider);
    	}
    }
    
    public void removeJumpPageMenuItemProvider(JumpPageMenuItemProvider provider) {
    	if(provider != null && provider.getName() != null) {
    		this.providers.remove(provider.getName());
    	}
    }
    
    private void loadMenus() {
    	this.menus.clear();
    	this.map.clear();
    	
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("menu.json");
            String menuJson = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(menuJson);
            this.menus = this.toMenuItems(array);
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        
        for(ActiveMenu menu : this.activeMenus.values()) {
        	menu.updateNavbarMenus();
        }
    }
    
    private void loadToolbar() {
    	this.toolbar.clear();
    	
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("toolbar.json");
            String toolbarJson = IOUtils.toString(in);
            JSONArray array = JSONArray.fromObject(toolbarJson);
            this.toolbar = this.toToolbarItems(array);
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        
        for(ActiveToolbar toolbar : this.activeToolbars.values()) {
        	toolbar.updateToolbarMenus();
        }
    }
    
    private List<MenuItem> toToolbarItems(JSONArray array) throws Exception {
        List<MenuItem> items = new ArrayList<MenuItem>();
        
        for(int i = 0; i < array.size(); i++) {
            MenuItem item = this.toMenuItem(array.getJSONObject(i));
            if(item != null) {
                items.add(item);
            }
        }
        
        return items;
    }
    
    private List<MenuItem> toMenuItems(JSONArray array) throws Exception {
        List<MenuItem> items = new ArrayList<MenuItem>();
        
        for(int i = 0; i < array.size(); i++) {
            MenuItem item = this.toMenuItem(array.getJSONObject(i));
            if(item != null) {
                items.add(item);
                this.registerMenuItem(item);
            }
        }
        
        return items;
    }
    
    private void registerMenuItem(MenuItem item) {
        String id = item.id;
        if(!StringUtils.isBlank(id)) {
            if(this.map.containsKey(id)) {
                logger.warn("duplicate menu id found [" + id + "]");
            }
            this.map.put(id, item);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private MenuItem toMenuItem(JSONObject object) throws Exception {
        MenuItem item = new MenuItem();
        
        if(object.containsKey("id")) {
            item.id = object.getString("id");
        }
        
        if(object.containsKey("label")) {
            item.label = object.getString("label");
        }
        
        if(object.containsKey("role")) {
            item.role = this.toMenuItemRole(object.getString("role"));
        }
        
        if(object.containsKey("link")) {
            item.link = object.getString("link");
        }
        
        if(object.containsKey("style")) {
            item.style = object.getString("style");
        }
        
        if(object.containsKey("provider")) {
            Class clazz = Class.forName(object.getString("provider"));
            MenuItemProvider provider = (MenuItemProvider) clazz.newInstance();
            item.provider = provider;
        }
        
        if(object.containsKey("data")) {
            JSONObject data = object.getJSONObject("data");
            for(Object key : data.keySet()) {
                String keyStr = (String)key;
                Object value = data.get(keyStr);
                item.data.put(keyStr, value);
            }
        }
        
        if(object.containsKey("menus")) {
            item.menus = this.toMenuItems(object.getJSONArray("menus"));
            
            for(MenuItem mi : item.menus) {
                mi.parent = item;
            }
        }
        
        return item;
    }
    
    public MenuItemRole toMenuItemRole(String role) {
        if("menu".equals(role)) {
            return MenuItemRole.Menu;
        }
        else if("menu-group".equals(role)) {
            return MenuItemRole.MenuGroup;
        }
        else if("separator".equals(role)) {
            return MenuItemRole.Separator;
        }
        else if("menu-group-placeholder".equals(role)) {
            return MenuItemRole.MenuGroupPlaceHolder;
        }
        else if("menu-placeholder".equals(role)) {
            return MenuItemRole.MenuPlaceHolder;
        }
        else {
            return null;
        }
    }
    
    public List<MenuItem> getMenuItems() {
        return this.menus;
    }
    
    public String generateNavbar() {
        StringBuffer sb = new StringBuffer();
        
        if(!this.menus.isEmpty()) {
            MenuItem active = this.menus.get(0);
            if(MenuItemRole.Menu == active.role) {
                sb.append(this.generateActiveMenuItem(active));
            }
            else {
                sb.append(this.generate(active));
            }
            
            for(int i = 1; i < this.menus.size(); i++) {
                sb.append(this.generate(this.menus.get(i)));
            }
        }
        
        return sb.toString();
    }
    
    public String generateToolbar() {
        StringBuffer sb = new StringBuffer();
        
        if(!this.toolbar.isEmpty()) {
            for(MenuItem item : this.toolbar) {
                if(MenuItemRole.Menu == item.role) {
                    sb.append(this.generateMenuItem(item));
                }
            }
        }
        
        return sb.toString();
    }
    
    private String generate(MenuItem item) {
        if(MenuItemRole.Separator == item.role) {
            return this.generateSeparator(item);
        }
        else if(MenuItemRole.Menu == item.role) {
            return this.generateMenuItem(item);
        }
        else if(MenuItemRole.MenuGroup == item.role) {
            if(item.parent == null) {
                return this.generateTopSubmenu(item);
            }
            else {
                return this.generateSubmenu(item);
            }
        }
        else if(MenuItemRole.MenuGroupPlaceHolder == item.role) {
            return this.generateMenuGroupPlaceHolder(item);
        }
        else if(MenuItemRole.MenuPlaceHolder == item.role) {
            return this.generateMenuPlaceHolder(item);
        }
        else {
            return "";
        }
    }
    
    private String generateMenuPlaceHolder(MenuItem item) {
        MenuItemProvider provider = item.provider;
        MenuItem mi = provider.getMenuItem();
        if(mi != null) {
            mi.parent = item.parent;
            mi.menus = item.menus;
            return this.generate(mi);
        }
        else {
            return "";
        }
    }
    
    private String generateMenuGroupPlaceHolder(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        MenuItemProvider provider = item.provider;
        List<MenuItem> items = provider.getMenuItems();
        if(items != null) {
            for(MenuItem mi : items) {
                sb.append(this.generate(mi));
                if(!this.map.containsKey(mi.id)) {
                    this.registerMenuItem(mi);
                }
            }
        }
        
        return sb.toString();
    }
    
    private String generateActiveMenuItem(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li data-id=\"");
        sb.append(item.id);
        sb.append("\" class=\"active\"><a href=\"");
        sb.append(item.link);
        sb.append("\">");
        sb.append(item.label);
        sb.append("</a></li>");
        
        return sb.toString();
    }
    
    private String generateTopSubmenu(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li data-id=\"");
        sb.append(item.id);
        sb.append("\" class=\"dropdown\">");
        sb.append("<a href=\"javascript:void(0)\" class=\"dropdown-toggle\" ");
        if(!StringUtils.isBlank(item.style)) {
            sb.append("style=\"" + item.style + "\"");
        }
        sb.append(" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">");
        sb.append(item.label);
        sb.append("<span class=\"caret\"></span></a>");
        sb.append("<ul class=\"dropdown-menu multi-level\">");
        for(MenuItem mi : item.menus) {
            sb.append(this.generate(mi));
        }
        sb.append("</ul></li>");
        
        return sb.toString();
    }
    
    private String generateSubmenu(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li data-id=\"");
        sb.append(item.id);
        sb.append("\" class=\"dropdown-submenu\">");
        sb.append("<a href=\"javascript:void(0)\" ");
        if(!StringUtils.isBlank(item.style)) {
            sb.append("style=\"" + item.style + "\"");
        }
        sb.append(">");
        sb.append(item.label);
        sb.append("</a>");
        sb.append("<ul class=\"dropdown-menu\">");
        for(MenuItem mi : item.menus) {
            sb.append(this.generate(mi));
        }
        sb.append("</ul></li>");
        
        return sb.toString();
    }
    
    private String generateSeparator(MenuItem item) {
        return "<li role=\"separator\" class=\"divider\"></li>";
    }
    
    private String generateMenuItem(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li data-id=\"");
        sb.append(item.id);
        sb.append("\"><a href=\"");
        sb.append(item.link);
        sb.append("\" ");
        if(!StringUtils.isBlank(item.style)) {
            sb.append("style=\"" + item.style + "\"");
        }
        sb.append(">");
        sb.append(item.label);
        sb.append("</a></li>");
        
        return sb.toString();
    }
    
    public MenuItem getMenuItem(String id) {
        if(StringUtils.isBlank(id)) {
            return null;
        }
        
        Map<String, MenuItem> items = this.getJumpPageExtMenuItems();
        MenuItem item = items.get(id);
        if(item != null) {
        	return item;
        }
        
        return this.map.get(id);
    }
    
    public List<String> getMatchingMenuIds(String id) {
        List<String> ret = new ArrayList<String>();
        
        List<String> menuIds = this.getSingleMenuIds();
        Collections.sort(menuIds);
        for(String menuId : menuIds) {
            if(StringUtils.containsIgnoreCase(menuId, id)) {
                ret.add(menuId);
            }
        }
        
        return ret;
    }
    
    public List<String> getMenuIds() {
        return new ArrayList<String>(this.map.keySet());
    }
    
    public List<String> getSingleMenuIds() {
        List<String> ret = new ArrayList<String>();
        
        for(MenuItem item : this.map.values()) {
            if(MenuItemRole.Menu == item.role) {
                ret.add(item.id);
            }
        }
        
        Map<String, MenuItem> items = this.getJumpPageExtMenuItems();
        ret.addAll(items.keySet());
        
        return ret;
    }
    
    public List<MenuInfo> queryMenuInfos(String text) {
    	List<MenuInfo> infos = new ArrayList<MenuInfo>();
    	if(StringUtils.isBlank(text)) {
    		return infos;
    	}
    	
    	for(MenuItem item : this.map.values()) {
    		if(MenuItemRole.Menu == item.role) {
    			if(item.id.contains(text) || item.label.contains(text) || item.link.contains(text)) {
    				MenuInfo info = new MenuInfo();
    				info.id = item.id;
    				info.label = item.label;
    				info.link = item.link;
    				infos.add(info);
    			}
    		}
    	}
    	
    	return infos;
    }

	@Override
	public Class<JumpPageMenuItemProvider> getExtensionClass() {
		return JumpPageMenuItemProvider.class;
	}

	@Override
	public void created(JumpPageMenuItemProvider t) {
		this.addJumpPageMenuItemProvider(t);
	}

	@Override
	public void removed(JumpPageMenuItemProvider t) {
		this.removeJumpPageMenuItemProvider(t);
	}

	@Override
	public void start() {
		this.loadMenus();
        this.loadToolbar();
	}

	@Override
	public void shutdown() {
	}
}

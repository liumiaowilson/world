package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemProvider;
import org.wilson.world.menu.MenuItemRole;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MenuManager {
    private static final Logger logger = Logger.getLogger(MenuManager.class);
    
    private static MenuManager instance;
    
    private List<MenuItem> menus = new ArrayList<MenuItem>();
    private Map<String, MenuItem> map = new HashMap<String, MenuItem>();
    
    private MenuManager() {
        this.loadMenus();
    }
    
    public static MenuManager getInstance() {
        if(instance == null) {
            instance = new MenuManager();
        }
        
        return instance;
    }
    
    private void loadMenus() {
        try {
            String menuJson = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("menu.json"));
            JSONArray array = JSONArray.fromObject(menuJson);
            this.menus = this.toMenuItems(array);
        }
        catch(Exception e) {
            logger.error(e);
        }
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
                this.registerMenuItem(mi);
            }
        }
        
        return sb.toString();
    }
    
    private String generateActiveMenuItem(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li class=\"active\"><a href=\"");
        sb.append(item.link);
        sb.append("\">");
        sb.append(item.label);
        sb.append("</a></li>");
        
        return sb.toString();
    }
    
    private String generateTopSubmenu(MenuItem item) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<li class=\"dropdown\">");
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
        
        sb.append("<li class=\"dropdown-submenu\">");
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
        
        sb.append("<li><a href=\"");
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
}

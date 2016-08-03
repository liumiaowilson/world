package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class SearchManager {
    private static SearchManager instance;
    
    private Map<String, ContentProvider> providers = new HashMap<String, ContentProvider>();
    
    private SearchManager() {
        
    }
    
    public static SearchManager getInstance() {
        if(instance == null) {
            instance = new SearchManager();
        }
        return instance;
    }
    
    public void registerContentProvider(ContentProvider provider) {
        if(provider != null) {
            this.providers.put(provider.getName(), provider);
        }
    }
    
    public void unregisterContentProvider(ContentProvider provider) {
        if(provider != null) {
            this.providers.remove(provider.getName());
        }
    }
    
    public List<String> getSearchTypes() {
        return new ArrayList<String>(this.providers.keySet());
    }
    
    public List<Content> search(String text, String type) {
        if(StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        
        List<Content> ret = new ArrayList<Content>();
        
        if(!StringUtils.isBlank(type)) {
            ContentProvider provider = this.providers.get(type);
            List<Content> contents = provider.search(text);
            for(Content content : contents) {
                content.link = type + "_edit.jsp?id=" + content.id;
                ret.add(content);
            }
        }
        else {
            for(Entry<String, ContentProvider> entry : this.providers.entrySet()) {
                String name = entry.getKey();
                ContentProvider provider = entry.getValue();
                List<Content> contents = provider.search(text);
                for(Content content : contents) {
                    content.link = name + "_edit.jsp?id=" + content.id;
                    ret.add(content);
                }
            }
        }
        
        return ret;
    }
}

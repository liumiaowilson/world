package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Idea;

public class MarkManager implements EventListener {
    private static MarkManager instance;
    
    private Map<String, List<String>> markedMap = new HashMap<String, List<String>>();
    
    private MarkManager() {
        EventManager.getInstance().registerListener(EventType.ClearTable, this);
        EventManager.getInstance().registerListener(EventType.DeleteIdea, this);
        EventManager.getInstance().registerListener(EventType.SplitIdea, this);
        EventManager.getInstance().registerListener(EventType.MergeIdea, this);
    }
    
    public static MarkManager getInstance() {
        if(instance == null) {
            instance = new MarkManager();
        }
        return instance;
    }
    
    public void mark(Object target) {
        if(target == null) {
            return;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return;
        }
        
        mark(type, id);
    }
    
    public void mark(String type, String id) {
        if(type == null) {
            return;
        }
        if(id == null) {
            return;
        }
        
        List<String> list = markedMap.get(type);
        if(list == null) {
            list = new ArrayList<String>();
            markedMap.put(type, list);
        }
        if(!list.contains(id)) {
            list.add(id);
        }
    }
    
    public void unmark(Object target) {
        if(target == null) {
            return;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return;
        }
        
        unmark(type, id);
    }
    
    public void unmark(String type, String id) {
        if(type == null) {
            return;
        }
        if(id == null) {
            return;
        }
        
        List<String> list = markedMap.get(type);
        if(list == null) {
            return;
        }
        list.remove(id);
    }
    
    public boolean isMarked(Object target) {
        if(target == null) {
            return false;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return false;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return false;
        }
        
        return isMarked(type, id);
    }
    
    public boolean isMarked(String type, String id) {
        if(!markedMap.containsKey(type)) {
            return false;
        }
        List<String> ids = markedMap.get(type);
        return ids != null && ids.contains(id);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getMarked(String type) {
        List<String> ret = markedMap.get(type);
        if(ret == null) {
            ret = Collections.EMPTY_LIST;
        }
        return ret;
    }
    
    public boolean hasMarked(String type) {
        return !this.getMarked(type).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(Event event) {
        if(EventType.ClearTable == event.type) {
            List<String> names = (List<String>) event.data.get("names");
            for(String name : names) {
                ItemTypeProvider provider = ItemManager.getInstance().getItemTypeProviderByItemTableName(name);
                if(provider != null) {
                    String itemTypeName = provider.getItemTypeName();
                    this.markedMap.remove(itemTypeName);
                }
            }
        }
        else if(EventType.DeleteIdea == event.type) {
            Idea idea = (Idea) event.data.get("data");
            this.unmark(idea);
        }
        else if(EventType.SplitIdea == event.type) {
            Idea idea = (Idea) event.data.get("old_data");
            this.unmark(idea);        
        }
        else if(EventType.MergeIdea == event.type) {
            List<Idea> ideas = (List<Idea>) event.data.get("data");
            for(Idea idea : ideas) {
                this.unmark(idea);
            }
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}

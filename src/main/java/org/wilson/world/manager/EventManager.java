package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class EventManager {
    private static EventManager instance;
    
    private Map<EventType, List<EventListener>> listeners = new HashMap<EventType, List<EventListener>>();
    
    private EventManager() {}
    
    public static EventManager getInstance() {
        if(instance == null) {
            instance = new EventManager();
        }
        return instance;
    }
    
    public void registerListener(EventType type, EventListener listener) {
        List<EventListener> list = this.listeners.get(type);
        if(list == null) {
            list = new ArrayList<EventListener>();
            this.listeners.put(type, list);
        }
        if(!list.contains(listener)) {
            list.add(listener);
        }
    }
    
    public void unregisterListener(EventType type, EventListener listener) {
        List<EventListener> list = this.listeners.get(type);
        if(list != null) {
            list.remove(listener);
        }
    }
    
    public void fireEvent(Event event) {
        if(event == null) {
            return;
        }
        if(event.type == null) {
            return;
        }
        List<EventListener> list = this.listeners.get(event.type);
        if(list != null) {
            for(EventListener listener : list) {
                listener.handle(event);
            }
        }
    }
}

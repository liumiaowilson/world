package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.event.EventWorker;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class EventManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(EventManager.class);
    
    private static EventManager instance;
    
    private Map<EventType, List<EventListener>> syncListeners = new HashMap<EventType, List<EventListener>>();
    private Map<EventType, List<EventListener>> asyncListeners = new HashMap<EventType, List<EventListener>>();
    
    private BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(20);
    private EventWorker worker = null;
    private Thread workerThread = null;
    
    private EventManager() {}
    
    public static EventManager getInstance() {
        if(instance == null) {
            instance = new EventManager();
        }
        return instance;
    }
    
    public void start() {
        worker = new EventWorker(queue, asyncListeners);
        workerThread = new Thread(worker);
        workerThread.start();
    }
    
    public void shutdown() {
        if(worker != null) {
            worker.setStopped(true);
        }
        try {
            if(workerThread != null) {
                workerThread.join();
            }
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
    
    public void registerListener(EventType type, EventListener listener) {
        if(listener.isAsync()) {
            this.registerAsyncListener(type, listener);
        }
        else {
            this.registerSyncListener(type, listener);
        }
    }
    
    private void registerSyncListener(EventType type, EventListener listener) {
        List<EventListener> list = this.syncListeners.get(type);
        if(list == null) {
            list = new ArrayList<EventListener>();
            this.syncListeners.put(type, list);
        }
        if(!list.contains(listener)) {
            list.add(listener);
        }
    }
    
    private void registerAsyncListener(EventType type, EventListener listener) {
        List<EventListener> list = this.asyncListeners.get(type);
        if(list == null) {
            list = new ArrayList<EventListener>();
            this.asyncListeners.put(type, list);
        }
        if(!list.contains(listener)) {
            list.add(listener);
        }
    }
    
    public void unregisterListener(EventType type, EventListener listener) {
        if(listener.isAsync()) {
            this.unregisterAsyncListener(type, listener);
        }
        else {
            this.unregisterSyncListener(type, listener);
        }
    }
    
    private void unregisterSyncListener(EventType type, EventListener listener) {
        List<EventListener> list = this.syncListeners.get(type);
        if(list != null) {
            list.remove(listener);
        }
    }
    
    private void unregisterAsyncListener(EventType type, EventListener listener) {
        List<EventListener> list = this.asyncListeners.get(type);
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
        List<EventListener> list = this.syncListeners.get(event.type);
        if(list != null) {
            for(EventListener listener : list) {
                listener.handle(event);
            }
        }
        
        this.queue.offer(event);
    }
}

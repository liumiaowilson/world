package org.wilson.world.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class EventWorker implements Runnable{
    private static final Logger logger = Logger.getLogger(EventWorker.class);
    
    private volatile boolean stopped = false;
    private BlockingQueue<Event> queue;
    private Map<EventType, List<EventListener>> listeners;
    
    public EventWorker(BlockingQueue<Event> queue, Map<EventType, List<EventListener>> listeners) {
        this.queue = queue;
        this.listeners = listeners;
    }
    
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    
    public boolean isStopped() {
        return this.stopped;
    }

    @Override
    public void run() {
        logger.info("Event worker is ready to handle events.");
        while(!this.stopped) {
            try {
                Event event = this.queue.poll(1, TimeUnit.SECONDS);
                if(event != null) {
                    List<EventListener> list = listeners.get(event.type);
                    if(list != null) {
                        for(EventListener listener : list) {
                            listener.handle(event);
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }
}

package org.wilson.world.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.wilson.world.thread.Worker;

public class EventWorker implements Worker {
    private static final Logger logger = Logger.getLogger(EventWorker.class);
    
    private volatile boolean stopped = false;
    private BlockingQueue<Event> queue;
    private Map<EventType, List<EventListener>> listeners;
    
    private long startTime;
    
    private long periods;
    private long workingTime;
    
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
        this.startTime = System.currentTimeMillis();
        
        while(!this.stopped) {
            try {
                Event event = this.queue.poll(1, TimeUnit.SECONDS);
                if(event != null) {
                    long start = System.currentTimeMillis();
                    
                    try {
                        List<EventListener> list = listeners.get(event.type);
                        if(list != null) {
                            for(EventListener listener : list) {
                                listener.handle(event);
                            }
                        }
                    }
                    catch(Exception e) {
                        logger.error(e);
                    }
                    finally {
                        this.workingTime += System.currentTimeMillis() - start;
                        this.periods++;
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public long getPeriods() {
        return periods;
    }

    @Override
    public long getTotalTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    @Override
    public long getWorkingTime() {
        return this.workingTime;
    }
}

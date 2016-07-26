package org.wilson.world.tick;

import java.util.ArrayList;
import java.util.List;

public class DefaultTickMonitor implements TickMonitor {
    private boolean ended = false;
    private List<TickMonitorListener> listeners = new ArrayList<TickMonitorListener>();
    
    @Override
    public void send(TickMessage message) {
        if(message == null) {
            return;
        }
        for(TickMonitorListener listener : this.listeners) {
            listener.messageSent(message);
        }
    }

    @Override
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    @Override
    public boolean isEnded() {
        return this.ended;
    }

    @Override
    public void clear() {
        for(TickMonitorListener listener : this.listeners) {
            listener.cleared();
        }
    }

    @Override
    public void addTickMonitorListener(TickMonitorListener listener) {
        if(listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
}

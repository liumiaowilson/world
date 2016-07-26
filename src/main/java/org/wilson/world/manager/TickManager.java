package org.wilson.world.manager;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.tick.AttackerInfo;
import org.wilson.world.tick.DefaultTickMonitor;
import org.wilson.world.tick.MessageInfo;
import org.wilson.world.tick.TickMessage;
import org.wilson.world.tick.TickMonitor;
import org.wilson.world.tick.TickMonitorListener;
import org.wilson.world.tick.Tickable;

public class TickManager implements TickMonitorListener{
    private static TickManager instance;
    
    public static final int MAX_STEP = 500;
    
    private List<Tickable> tickables = new ArrayList<Tickable>();
    private TickMonitor monitor = null;
    
    private List<MessageInfo> messages = new ArrayList<MessageInfo>();
    
    private TickManager() {
        this.monitor = new DefaultTickMonitor();
        this.monitor.addTickMonitorListener(this);
    }
    
    public static TickManager getInstance() {
        if(instance == null) {
            instance = new TickManager();
        }
        return instance;
    }
    
    public void addTickable(Tickable tickable) {
        if(tickable == null) {
            return;
        }
        this.tickables.add(tickable);
    }
    
    public void reset() {
        this.tickables.clear();
        this.messages.clear();
        this.monitor.setEnded(false);
    }
    
    public void start() {
        this.monitor.clear();
        for(Tickable tickable : this.tickables) {
            tickable.setTurnId(1);
        }
        
        if(this.tickables.size() == 2) {
            TickMessage startMsg = new TickMessage();
            startMsg.source = this.tickables.get(0);
            startMsg.target = this.tickables.get(1);
            startMsg.message = "Game started.";
            this.monitor.send(startMsg);
        }
    }
    
    public void end() {
        this.tickables.clear();
    }
    
    public void play() {
        this.start();
        
        int step = 0;
        while(this.next() && step < MAX_STEP) {
            step += 1;
        }
        
        this.end();
    }
    
    public boolean next() {
        Collections.sort(this.tickables, new Comparator<Tickable>(){

            @Override
            public int compare(Tickable o1, Tickable o2) {
                return o1.getTurnId() - o2.getTurnId();
            }
            
        });
        
        for(Tickable obj : this.tickables) {
            int turn = obj.getTurnId();
            int ret = obj.tick(this.monitor);
            if(this.monitor.isEnded()) {
                return false;
            }
            obj.setTurnId(turn + ret);
        }
        
        return true;
    }
    
    public String display(TickMessage message) {
        StringBuffer sb = new StringBuffer("");
        if(message != null) {
            if(message.source != null) {
                sb.append("[" + message.source.toString() + "]");
            }
            else {
                sb.append("Unknown");
            }
            if(message.target != null) {
                sb.append(" -> [" + message.target.toString() + "]");
            }
            sb.append(":");
            if(message.message != null) {
                sb.append(message.message);
            }
            else {
                sb.append("");
            }
        }
        return sb.toString();
    }
    
    public void addOutput(final PrintStream out) {
        this.monitor.addTickMonitorListener(new TickMonitorListener(){

            @Override
            public void messageSent(TickMessage message) {
                out.println(display(message));
            }

            @Override
            public void cleared() {
            }
        });
    }
    
    public List<Tickable> getTargets(Tickable tickable) {
        if(tickable == null) {
            return Collections.emptyList();
        }
        
        List<Tickable> ret = new ArrayList<Tickable>();
        
        for(Tickable t : this.tickables) {
            if(!t.equals(tickable)) {
                ret.add(t);
            }
        }
        
        return ret;
    }
    
    public Tickable getTarget(Tickable tickable) {
        List<Tickable> targets = this.getTargets(tickable);
        if(targets.isEmpty()) {
            return null;
        }
        else {
            return targets.get(0);
        }
    }

    @Override
    public void messageSent(TickMessage message) {
        if(message != null) {
            MessageInfo info = new MessageInfo();
            if(message.source != null) {
                info.source = (AttackerInfo) message.source.getInfo();
            }
            if(message.target != null) {
                info.target = (AttackerInfo) message.target.getInfo();
            }
            info.message = display(message);
            this.messages.add(info);
        }
    }

    @Override
    public void cleared() {
        this.messages.clear();
    }
    
    public List<MessageInfo> getMessages() {
        return this.messages;
    }
}

package org.wilson.world.item;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.NotesManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;

public class NotesMonitor implements MonitorParticipant {
    private Alert alert = null;
    
    public NotesMonitor() {
        this.alert = new Alert();
        this.alert.id = "Notes found";
        this.alert.message = "Please process the notes as soon as possible.";
        this.alert.canAck = true;
    }
    
    @Override
    public boolean isOK() {
        String notes = NotesManager.getInstance().getNotes();
        return StringUtils.isBlank(notes);
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}

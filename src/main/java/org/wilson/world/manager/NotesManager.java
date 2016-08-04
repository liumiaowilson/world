package org.wilson.world.manager;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.item.NotesMonitor;

public class NotesManager {
    private static NotesManager instance;
    
    private String notes;
    
    private static final int NOTES_LIMIT = 400;
    
    private NotesManager() {
        MonitorManager.getInstance().registerMonitorParticipant(new NotesMonitor());
    }
    
    public static NotesManager getInstance() {
        if(instance == null) {
            instance = new NotesManager();
        }
        return instance;
    }
    
    public String getNotes() {
        return this.notes;
    }
    
    public void setNotes(String notes) {
        if(StringUtils.isBlank(notes)) {
            return;
        }
        if(notes.length() > NOTES_LIMIT) {
            this.notes = notes.substring(0, NOTES_LIMIT);
        }
        else {
            this.notes = notes;
        }
    }
}

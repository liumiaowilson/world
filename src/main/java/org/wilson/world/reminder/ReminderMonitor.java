package org.wilson.world.reminder;

import java.util.List;

import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.ReminderManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Reminder;
import org.wilson.world.monitor.MonitorParticipant;

public class ReminderMonitor implements MonitorParticipant {
    private Alert alert;
    
    public ReminderMonitor() {
        this.alert = new Alert();
        this.alert.id = "Reminders detected";
        this.alert.canAck = true;
    }
    
    @Override
    public boolean isOK() {
        List<Reminder> reminders = ReminderManager.getInstance().getExpiredReminders();
        if(reminders.isEmpty()) {
            return true;
        }
        
        StringBuilder sb = new StringBuilder();
        for(Reminder reminder : reminders) {
            ReminderManager.getInstance().deleteReminder(reminder.id);
            sb.append(reminder.message);
            sb.append("<br/>");
        }
        
        String msg = sb.toString();
        this.alert.message = msg;
        NotifyManager.getInstance().notifyReminder(msg);
        
        return false;
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}

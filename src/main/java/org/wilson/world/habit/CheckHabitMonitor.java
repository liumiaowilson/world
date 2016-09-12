package org.wilson.world.habit;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.HabitManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Habit;
import org.wilson.world.monitor.MonitorParticipant;

public class CheckHabitMonitor implements MonitorParticipant {
    private static final Logger logger = Logger.getLogger(CheckHabitMonitor.class);
    
    private Alert alert;
    
    private int startHour = 23;
    private int startMinute = 30;
    private int endHour = 23;
    private int endMinute = 59;
    
    public CheckHabitMonitor() {
        this.alert = new Alert();
        this.alert.id = "Unchecked habits found";
        this.alert.message = "Please check these habits as soon as possible";
        this.alert.canAck = true;
        this.alert.url = "habit_trace_check.jsp";
        
        try {
            String range = ConfigManager.getInstance().getConfig("habit.monitor.range", "23:30-23:59");
            String [] parts = range.split("-");
            
            String [] items = parts[0].split(":");
            startHour = Integer.parseInt(items[0]);
            startMinute = Integer.parseInt(items[1]);
            
            items = parts[1].split(":");
            endHour = Integer.parseInt(items[0]);
            endMinute = Integer.parseInt(items[1]);
        }
        catch(Exception e) {
            logger.error(e);
        }
    }
    
    @Override
    public boolean isOK() {
        TimeZone tz = ConfigManager.getInstance().getUserDefaultTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        
        if(hour >= startHour && minute >= startMinute && hour <= endHour && minute <= endMinute) {
            List<Habit> habits = HabitManager.getInstance().getCheckableHabits(tz);
            if(!habits.isEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}

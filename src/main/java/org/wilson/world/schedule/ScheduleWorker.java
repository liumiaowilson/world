package org.wilson.world.schedule;

import java.util.Date;
import java.util.List;

import org.wilson.world.manager.ScheduleManager;
import org.wilson.world.thread.DefaultWorker;
import org.wilson.world.util.TimeUtils;

public class ScheduleWorker extends DefaultWorker {

    public ScheduleWorker() {
        this.setPeriodTime(TimeUtils.HOUR_DURATION);
    }
    
    @Override
    public void work() throws Exception {
        List<ScheduledJob> jobs = ScheduleManager.getInstance().getJobs();
        for(ScheduledJob job : jobs) {
            Date next = job.getNextStartDate();
            if(next.getTime() <= System.currentTimeMillis()) {
                job.doJob();
            }
        }
    }

}

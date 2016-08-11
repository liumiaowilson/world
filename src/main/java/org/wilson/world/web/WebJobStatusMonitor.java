package org.wilson.world.web;

import java.util.List;

import org.wilson.world.manager.WebManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;

public class WebJobStatusMonitor implements MonitorParticipant {
    private Alert alert;
    
    public WebJobStatusMonitor() {
        this.alert = new Alert();
        this.alert.id = "Web Job Broken";
        this.alert.message = "Web jobs with unexpected status are found. Please check them.";
        this.alert.canAck = true;
        this.alert.url = "web_job_info.jsp";
    }
    
    @Override
    public boolean isOK() {
        List<WebJob> jobs = WebManager.getInstance().getJobs();
        for(WebJob job : jobs) {
            String status = WebManager.getInstance().getJobStatus(job);
            if(WebJobStatus.Inactive.name().equals(status) || WebJobStatus.Error.name().equals(status)) {
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

package org.wilson.world.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.thread.DefaultWorker;
import org.wilson.world.util.TimeUtils;

public class WebJobWorker extends DefaultWorker {
    private boolean firstTime = true;
    
    public WebJobWorker() {
        this.setPeriodTime(TimeUtils.HOUR_DURATION);
    }

    @Override
    public void work() throws Exception {
        List<WebJob> jobs = WebManager.getInstance().getJobs();
        Collections.sort(jobs, new Comparator<WebJob>(){

            @Override
            public int compare(WebJob o1, WebJob o2) {
                return -(o1.getId() - o2.getId());
            }
            
        });
        for(final WebJob job : jobs) {
            String status = WebManager.getInstance().getJobStatus(job);
            if(WebJobStatus.Disabled.name().equals(status)) {
                continue;
            }
            int period = job.getPeriod();
            final long now = System.currentTimeMillis();
            long last = WebManager.getInstance().getLastTime(job);
            if(last > 0) {
                if(last + TimeUtils.HOUR_DURATION * period > now) {
                    if(!firstTime) {
                        continue;
                    }
                }
            }
            
            ThreadPoolManager.getInstance().execute(new Runnable(){

                @Override
                public void run() {
                    WebManager.getInstance().run(job, now);
                }
                
            });
        }
        
        this.firstTime = false;
        
        while(true) {
            if(WebManager.getInstance().allJobsRun()) {
                break;
            }
            
            Thread.sleep(TimeUtils.MINUTE_DURATION);
        }
        
        Map<String, Integer> data = WebManager.getInstance().getDataSizeStats();
        DataSizeInfo info = new DataSizeInfo();
        info.data = data;
        WebManager.getInstance().addDataSizeInfo(info);
    }

}

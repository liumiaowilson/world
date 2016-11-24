package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.JavaObject;
import org.wilson.world.java.JavaObjectListener;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.JobInfo;
import org.wilson.world.schedule.ScheduleWorker;
import org.wilson.world.schedule.ScheduledJob;
import org.wilson.world.util.FormatUtils;

public class ScheduleManager implements ManagerLifecycle, JavaObjectListener {
    private static final Logger logger = Logger.getLogger(ScheduleManager.class);
    
    private static ScheduleManager instance;
    
    private List<ScheduledJob> jobs = new ArrayList<ScheduledJob>();
    private ScheduleWorker worker = null;
    private Thread workerThread = null;
    
    private ScheduleManager() {
        JavaObjectManager.getInstance().addJavaObjectListener(this);
    }
    
    public static ScheduleManager getInstance() {
        if(instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    @Override
    public void start() {
        worker = new ScheduleWorker();
        workerThread = new Thread(worker);
        workerThread.start();
    }

    @Override
    public void shutdown() {
        if(worker != null) {
            worker.setStopped(true);
        }
        try {
            if(workerThread != null) {
                workerThread.interrupt();
                workerThread.join();
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }
    
    public List<ScheduledJob> getJobs() {
        return this.jobs;
    }
    
    public void addJob(ScheduledJob job) {
        if(job != null) {
            this.jobs.add(job);
        }
    }
    
    public void removeJob(ScheduledJob job) {
        if(job != null) {
            this.jobs.remove(job);
        }
    }
    
    public List<JobInfo> getJobInfos(TimeZone tz) {
        List<JobInfo> infos = new ArrayList<JobInfo>();
        
        for(ScheduledJob job : this.jobs) {
            JobInfo info = new JobInfo();
            info.name = job.getJobName();
            Date date = job.getNextStartDate();
            info.nextTime = FormatUtils.format(date, tz);
            infos.add(info);
        }
        
        Collections.sort(infos, new Comparator<JobInfo>(){
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        
        return infos;
    }
    
    public void run(String jobName) {
        if(StringUtils.isBlank(jobName)) {
            return;
        }
        
        ScheduledJob job = null;
        for(ScheduledJob j : this.jobs) {
            if(j.getJobName().equals(jobName)) {
                job = j;
                break;
            }
        }
        if(job == null) {
            return;
        }
        
        job.doJob();
    }

	@Override
	public void created(JavaObject javaObject) {
		if(javaObject != null && javaObject.object != null) {
			if(javaObject.object instanceof ScheduledJob) {
				this.addJob((ScheduledJob) javaObject.object);
			}
		}
	}

	@Override
	public void removed(JavaObject javaObject) {
		if(javaObject != null && javaObject.object != null) {
			if(javaObject.object instanceof ScheduledJob) {
				this.removeJob((ScheduledJob) javaObject.object);
			}
		}
	}
}

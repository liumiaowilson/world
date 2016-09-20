package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Period;
import org.wilson.world.period.PeriodRecord;
import org.wilson.world.period.PeriodReport;
import org.wilson.world.period.PeriodStatus;
import org.wilson.world.period.PeriodTodayContentProvider;
import org.wilson.world.period.PurgePeriodJob;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;

public class PeriodManager implements ItemTypeProvider {
    public static final String NAME = "period";
    
    private static PeriodManager instance;
    
    private DAO<Period> dao = null;
    
    @SuppressWarnings("unchecked")
    private PeriodManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Period.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        ScheduleManager.getInstance().addJob(new PurgePeriodJob());
        
        TodayManager.getInstance().addTodayContentProvider(new PeriodTodayContentProvider());
    }
    
    public static PeriodManager getInstance() {
        if(instance == null) {
            instance = new PeriodManager();
        }
        return instance;
    }
    
    public void createPeriod(Period period) {
        ItemManager.getInstance().checkDuplicate(period);
        
        this.dao.create(period);
    }
    
    public Period getPeriod(int id) {
        Period period = this.dao.get(id);
        if(period != null) {
            return period;
        }
        else {
            return null;
        }
    }
    
    public List<Period> getPeriods() {
        List<Period> result = new ArrayList<Period>();
        for(Period period : this.dao.getAll()) {
            result.add(period);
        }
        return result;
    }
    
    public void updatePeriod(Period period) {
        this.dao.update(period);
    }
    
    public void deletePeriod(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Period;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Period period = (Period)target;
        return String.valueOf(period.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return this.getID(target);
    }
    
    public List<String> getPeriodStatuses() {
        List<String> ret = new ArrayList<String>();
        
        for(PeriodStatus status : PeriodStatus.values()) {
            ret.add(status.name());
        }
        
        return ret;
    }
    
    public List<Period> getSortedPeriods() {
        List<Period> periods = this.getPeriods();
        
        Collections.sort(periods, new Comparator<Period>(){

            @Override
            public int compare(Period o1, Period o2) {
                if(o1.time < o2.time) {
                    return -1;
                }
                else if(o1.time > o2.time) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
            
        });
        
        return periods;
    }
    
    public Period getLastPeriod() {
        List<Period> periods = this.getSortedPeriods();
        if(periods.isEmpty()) {
            return null;
        }
        else {
            return periods.get(periods.size() - 1);
        }
    }
    
    public boolean isOnPeriod() {
        Period period = this.getLastPeriod();
        
        if(period != null) {
            if(PeriodStatus.Start.name().equals(period.status)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<PeriodRecord> getPeriodRecords() {
        List<PeriodRecord> ret = new ArrayList<PeriodRecord>();
        
        List<Period> periods = this.getSortedPeriods();
        for(int i = 0; i < periods.size(); i++) {
            Period period = periods.get(i);
            if(PeriodStatus.End.name().equals(period.status)) {
                continue;
            }
            if(i < periods.size() - 1) {
                Period next = periods.get(i + 1);
                if(PeriodStatus.Start.name().equals(next.status)) {
                    continue;
                }
                
                PeriodRecord record = new PeriodRecord();
                record.start = period.time;
                record.end = next.time;
                ret.add(record);
            }
        }
        
        return ret;
    }
    
    public PeriodReport getPeriodReport() {
        List<PeriodRecord> records = this.getPeriodRecords();
        if(records.isEmpty()) {
            return null;
        }
        
        long total = 0;
        for(PeriodRecord record : records) {
            total += record.end - record.start;
        }
        
        PeriodReport report = new PeriodReport();
        report.durationDays = FormatUtils.getRoundedValue(total * 1.0 / (records.size() * TimeUtils.DAY_DURATION));
        if(records.size() == 1) {
            report.cycleDays = -1;
        }
        else {
            report.cycleDays = FormatUtils.getRoundedValue((records.get(records.size() - 1).start - records.get(0).start) * 1.0 / ((records.size() - 1) * TimeUtils.DAY_DURATION));
        }
        
        return report;
    }
}

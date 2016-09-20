package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Period;
import org.wilson.world.period.PeriodStatus;
import org.wilson.world.period.PurgePeriodJob;

public class PeriodManager implements ItemTypeProvider {
    public static final String NAME = "period";
    
    private static PeriodManager instance;
    
    private DAO<Period> dao = null;
    
    @SuppressWarnings("unchecked")
    private PeriodManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Period.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        ScheduleManager.getInstance().addJob(new PurgePeriodJob());
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
}

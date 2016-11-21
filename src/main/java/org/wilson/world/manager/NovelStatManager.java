package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.model.NovelStat;
import org.wilson.world.novel.PurgeNStatsJob;

public class NovelStatManager {
    private static NovelStatManager instance;
    
    private DAO<NovelStat> dao = null;
    
    @SuppressWarnings("unchecked")
    private NovelStatManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelStat.class);
        
        ScheduleManager.getInstance().addJob(new PurgeNStatsJob());
    }
    
    public static NovelStatManager getInstance() {
        if(instance == null) {
            instance = new NovelStatManager();
        }
        return instance;
    }
    
    public void createNovelStat(NovelStat stat) {
        this.dao.create(stat);
    }
    
    public NovelStat getNovelStat(int id) {
    	NovelStat stat = this.dao.get(id);
        if(stat != null) {
            return stat;
        }
        else {
            return null;
        }
    }
    
    public List<NovelStat> getNovelStats() {
        List<NovelStat> result = new ArrayList<NovelStat>();
        for(NovelStat stat : this.dao.getAll()) {
            result.add(stat);
        }
        return result;
    }
    
    public void updateNovelStat(NovelStat stat) {
        this.dao.update(stat);
    }
    
    public void deleteNovelStat(int id) {
        this.dao.delete(id);
    }
    
    @SuppressWarnings("rawtypes")
	public DAO getDAO() {
        return this.dao;
    }
}

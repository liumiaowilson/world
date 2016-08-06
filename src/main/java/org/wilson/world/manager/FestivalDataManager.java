package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.festival.DefaultFestival;
import org.wilson.world.festival.Festival;
import org.wilson.world.festival.FestivalEngine;
import org.wilson.world.festival.FestivalEngineFactory;
import org.wilson.world.festival.FestivalFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CalendarEvent;
import org.wilson.world.model.FestivalData;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.TimeUtils;

public class FestivalDataManager implements ItemTypeProvider {
    public static final String NAME = "festival_data";
    
    private static FestivalDataManager instance;
    
    private DAO<FestivalData> dao = null;
    
    private Cache<Integer, Festival> festivals = null;
    
    private static int GLOBAL_ID = 1;
    
    @SuppressWarnings("unchecked")
    private FestivalDataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(FestivalData.class);
        this.festivals = new DefaultCache<Integer, Festival>("festival_data_manager_festivals", false);
        ((CachedDAO<FestivalData>)this.dao).getCache().addCacheListener(new CacheListener<FestivalData>(){

            @Override
            public void cachePut(FestivalData old, FestivalData v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                loadFestivalData(v);
            }

            @Override
            public void cacheDeleted(FestivalData v) {
                FestivalDataManager.this.festivals.delete(v.id);
            }

            @Override
            public void cacheLoaded(List<FestivalData> all) {
                loadSystemFestivals();
            }

            @Override
            public void cacheLoading(List<FestivalData> old) {
                FestivalDataManager.this.festivals.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(FestivalData data : getFestivalDatas()) {
                    boolean found = data.name.contains(text) || data.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = data.id;
                        content.name = data.name;
                        content.description = data.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static FestivalDataManager getInstance() {
        if(instance == null) {
            instance = new FestivalDataManager();
        }
        return instance;
    }
    
    private void loadSystemFestivals() {
        GLOBAL_ID = 1;
        
        for(Festival festival : FestivalFactory.getInstance().getFestivals()) {
            festival.setId(-GLOBAL_ID++);
            this.festivals.put(festival.getId(), festival);
        }
    }
    
    private void loadFestivalData(FestivalData data) {
        if(data != null) {
            DefaultFestival festival = new DefaultFestival(data);
            this.festivals.put(festival.getId(), festival);
        }
    }
    
    public void createFestivalData(FestivalData data) {
        ItemManager.getInstance().checkDuplicate(data);
        
        this.dao.create(data);
    }
    
    public FestivalData getFestivalData(int id) {
        FestivalData data = this.dao.get(id);
        if(data != null) {
            return data;
        }
        else {
            return null;
        }
    }
    
    public List<FestivalData> getFestivalDatas() {
        List<FestivalData> result = new ArrayList<FestivalData>();
        for(FestivalData data : this.dao.getAll()) {
            result.add(data);
        }
        return result;
    }
    
    public void updateFestivalData(FestivalData data) {
        this.dao.update(data);
    }
    
    public void deleteFestivalData(int id) {
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
        return target instanceof FestivalData;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        FestivalData data = (FestivalData)target;
        return String.valueOf(data.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public List<Date> getDates(String definition, int yearFrom, int yearTo, TimeZone tz) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        if(StringUtils.isBlank(definition)) {
            return Collections.emptyList();
        }
        
        int pos = definition.indexOf(":");
        if(pos < 0) {
            return Collections.emptyList();
        }
        
        String name = definition.substring(0, pos);
        String pattern = definition.substring(pos + 1);
        if(StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        
        FestivalEngine engine = FestivalEngineFactory.getInstance().getEngineByName(name);
        if(engine == null) {
            return Collections.emptyList();
        }
        
        return engine.getDates(pattern, yearFrom, yearTo, tz);
    }
    
    public List<Festival> getSystemFestivals() {
        return FestivalFactory.getInstance().getFestivals();
    }
    
    public List<Festival> getFestivals() {
        return this.festivals.getAll();
    }
    
    public Festival getFestival(int id) {
        return this.festivals.get(id);
    }
    
    public List<CalendarEvent> getCalendarEvents(TimeZone tz) {
        return this.getCalendarEvents(tz, 1);
    }
    
    public List<CalendarEvent> getCalendarEvents(TimeZone tz, int year_range) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        int year = cal.get(Calendar.YEAR);
        int yearFrom = year - year_range;
        int yearTo = year + year_range;
        
        List<CalendarEvent> ret = new ArrayList<CalendarEvent>();
        for(Festival festival : this.getFestivals()) {
            List<Date> dates = festival.getDates(yearFrom, yearTo, tz);
            for(Date date : dates) {
                CalendarEvent event = new CalendarEvent();
                event.title = festival.getName();
                event.start = TimeUtils.toDateString(date, tz);
                ret.add(event);
            }
        }
        return ret;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        FestivalData data = (FestivalData)target;
        return data.name;
    }
}
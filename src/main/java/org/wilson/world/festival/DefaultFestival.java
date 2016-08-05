package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.manager.FestivalDataManager;
import org.wilson.world.model.FestivalData;

public class DefaultFestival implements Festival {
    private FestivalData data;
    
    public DefaultFestival(FestivalData data) {
        this.data = data;
    }
    
    @Override
    public void setId(int id) {
    }

    @Override
    public int getId() {
        return data.id;
    }

    @Override
    public String getName() {
        return data.name;
    }

    @Override
    public String getDescription() {
        return data.description;
    }

    @Override
    public List<Date> getDates(int yearFrom, int yearTo, TimeZone tz) {
        return FestivalDataManager.getInstance().getDates(data.definition, yearFrom, yearTo, tz);
    }

}

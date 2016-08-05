package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.manager.FestivalDataManager;

public class SystemFestival implements Festival {
    private int id;
    private String name;
    private String description;
    private String definition;
    
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<Date> getDates(int yearFrom, int yearTo, TimeZone tz) {
        return FestivalDataManager.getInstance().getDates(definition, yearFrom, yearTo, tz);
    }

}

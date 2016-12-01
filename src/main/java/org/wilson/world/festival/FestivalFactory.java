package org.wilson.world.festival;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.manager.ContactAttrDefManager;
import org.wilson.world.manager.ContactManager;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.model.Contact;
import org.wilson.world.period.PeriodEndFestival;
import org.wilson.world.period.PeriodStartFestival;

public class FestivalFactory implements JavaExtensionListener<AbstractFestival> {
    private static FestivalFactory instance;
    
    private List<Festival> festivals = new ArrayList<Festival>();
    
    private FestivalFactory() {
    	ExtManager.getInstance().addJavaExtensionListener(this);
    	
        this.loadFestivals();
    }
    
    public static FestivalFactory getInstance() {
        if(instance == null) {
            instance = new FestivalFactory();
        }
        return instance;
    }
    
    private void loadSystemFestivals() {
    	this.festivals.add(new PeriodStartFestival());
    	this.festivals.add(new PeriodEndFestival());
    }
    
    private void loadFestivals() {
        this.festivals.add(this.buildFestival("New Year's Day", "The first day of the year.", "solar:1/1"));
        this.festivals.add(this.buildFestival("Spring Festival", "The first day of the year for traditional Chinese.", "lunar:1/1"));
        this.festivals.add(this.buildFestival("Lantern Festival", "The day when people watch lanterns.", "lunar:1/15"));
        this.festivals.add(this.buildFestival("Valentines' Day", "The day to celebrate lovers.", "solar:2/14"));
        this.festivals.add(this.buildFestival("Women's Day", "The day to celebrate women.", "solar:3/8"));
        this.festivals.add(this.buildFestival("Fools' Day", "The day to fool others.", "solar:4/1"));
        this.festivals.add(this.buildFestival("Labors' Day", "The day to celebrate labors.", "solar:5/1"));
        this.festivals.add(this.buildFestival("Easter's Day", "The day to celebrate the revival of Jesus.", "easter:easter"));
        this.festivals.add(this.buildFestival("Mothers' Day", "The day to celebrate mothers.", "week:5/2/7"));
        this.festivals.add(this.buildFestival("Dragon-boat Festival", "The day to celebrate Qu Yuan.", "lunar:5/5"));
        this.festivals.add(this.buildFestival("Chidren's Day", "The day to celebrate children.", "solar:6/1"));
        this.festivals.add(this.buildFestival("Fathers' Day", "The day to celebrate fathers.", "week:6/3/7"));
        this.festivals.add(this.buildFestival("Chinese Valentines' Day", "The day to celebrate lovers for Chinese.", "lunar:7/7"));
        this.festivals.add(this.buildFestival("Mid-autumn Festival", "The day to eat mooncakes.", "lunar:8/15"));
        this.festivals.add(this.buildFestival("Teachers' Day", "The day to celebrate teachers.", "solar:9/10"));
        this.festivals.add(this.buildFestival("Double Ninth Festival", "The day to celebrate the old.", "lunar:9/9"));
        this.festivals.add(this.buildFestival("National Day", "The day to celebrate the birth of PRC.", "solar:10/1"));
        this.festivals.add(this.buildFestival("Halloween Day", "The day to play treat or trick.", "solar:10/31"));
        this.festivals.add(this.buildFestival("Thanksgiving Day", "The day to thank others.", "week:11/4/4"));
        this.festivals.add(this.buildFestival("Christmas Day", "The day to celebrate the birth of Jesus.", "solar:12/25"));
        this.festivals.add(this.buildFestival("Poverty Day", "The day to remember hard times", "month:2/6"));
        
        this.loadBirthdayFestivals();
        
        this.loadSystemFestivals();
    }
    
    private void loadBirthdayFestivals() {
        for(Contact contact : ContactManager.getInstance().getContacts()) {
            Festival festival = this.buildBirthdayFestival(contact);
            if(festival != null) {
                this.festivals.add(festival);
            }
        }
    }
    
    public Festival buildBirthdayFestival(Contact contact) {
        if(contact == null) {
            return null;
        }
        
        String birthday = contact.getValue(ContactAttrDefManager.DEF_BIRTHDAY);
        if(StringUtils.isBlank(birthday)) {
            return null;
        }
        
        String [] items = birthday.split("-");
        String pattern = items[1].trim() + "/" + items[2].trim();
        String definition = SolarFestivalEngine.NAME + ":" + pattern;
        
        SystemFestival festival = new SystemFestival();
        festival.setName("Birthday for [" + contact.name + "]");
        festival.setDescription("Birthday for [" + contact.name + "]");
        festival.setDefinition(definition);
        return festival;
    }
    
    public Festival buildFestival(String name, String description, String definition) {
        SystemFestival festival = new SystemFestival();
        festival.setName(name);
        festival.setDescription(description);
        festival.setDefinition(definition);
        return festival;
    }
    
    public List<Festival> getFestivals() {
        return this.festivals;
    }

	@Override
	public Class<AbstractFestival> getExtensionClass() {
		return AbstractFestival.class;
	}

	@Override
	public void created(AbstractFestival t) {
		if(t != null) {
			this.festivals.add(t);
		}
	}

	@Override
	public void removed(AbstractFestival t) {
		if(t != null) {
			this.festivals.remove(t);
		}
	}
}

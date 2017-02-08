package org.wilson.world.period;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.festival.AbstractFestival;
import org.wilson.world.manager.PeriodManager;

public class PeriodEndFestival extends AbstractFestival {
	public PeriodEndFestival() {
		this.setName("Period End");
		this.setDescription("The time when her period ends.");
	}
	
	@Override
	public List<Date> getDates(int yearFrom, int yearTo, TimeZone tz) {
		List<Date> dates = new ArrayList<Date>();
		PeriodRecord record = PeriodManager.getInstance().getNextExpectedPeriodRecord();
		if(record != null) {
			dates.add(new Date(record.end));
		}
		
		return dates;
	}

}

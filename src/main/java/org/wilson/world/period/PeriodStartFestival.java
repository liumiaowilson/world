package org.wilson.world.period;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.festival.AbstractFestival;
import org.wilson.world.manager.PeriodManager;

public class PeriodStartFestival extends AbstractFestival {
	public PeriodStartFestival() {
		this.setName("Period Start");
		this.setDescription("The time when her period starts.");
	}
	
	@Override
	public List<Date> getDates(int yearFrom, int yearTo, TimeZone tz) {
		List<Date> dates = new ArrayList<Date>();
		PeriodRecord record = PeriodManager.getInstance().getNextExpectedPeriodRecord();
		if(record != null) {
			dates.add(new Date(record.start));
		}
		
		return dates;
	}

}

package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class WeekFestivalEngineTest {

    @Test
    public void test() {
        WeekFestivalEngine engine = new WeekFestivalEngine();
        List<Date> dates = engine.getDates("8/-1/1", 2015, 2017, TimeZone.getDefault());
        for(Date date : dates) {
            System.out.println(date);
        }
    }

}

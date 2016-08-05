package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class SolarFestivalEngineTest {

    @Test
    public void test() {
        SolarFestivalEngine engine = new SolarFestivalEngine();
        List<Date> dates = engine.getDates("7/1", 2015, 2017, TimeZone.getDefault());
        for(Date date : dates) {
            System.out.println(date);
        }
    }

}

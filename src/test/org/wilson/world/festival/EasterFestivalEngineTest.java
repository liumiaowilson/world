package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class EasterFestivalEngineTest {

    @Test
    public void test() {
        EasterFestivalEngine engine = new EasterFestivalEngine();
        List<Date> dates = engine.getDates("test", 2015, 2017, TimeZone.getDefault());
        for(Date date : dates) {
            System.out.println(date);
        }
    }

}

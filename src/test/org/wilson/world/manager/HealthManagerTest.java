package org.wilson.world.manager;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.wilson.world.util.TimeUtils;

public class HealthManagerTest {

    @Test
    public void testBioCurves() throws ParseException {
        Date birth = TimeUtils.fromDateString("1987-04-06", TimeZone.getDefault());
        long elapsed = System.currentTimeMillis() - birth.getTime();
        
        int pct = HealthManager.getInstance().getBioCurveValueAsPercentage(elapsed, HealthManager.PERIOD_ENERGY);
        System.out.println(pct);
        
        pct = HealthManager.getInstance().getBioCurveValueAsPercentage(elapsed, HealthManager.PERIOD_EMOTION);
        System.out.println(pct);
        
        pct = HealthManager.getInstance().getBioCurveValueAsPercentage(elapsed, HealthManager.PERIOD_INTELLIGENCE);
        System.out.println(pct);
    }

}

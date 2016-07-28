package org.wilson.world.util;

import org.junit.Test;

public class LunarSolarConverterTest {

    @Test
    public void testFromSolarToLunar() {
        Solar solar = new Solar();
        solar.solarYear = 2016;
        solar.solarMonth = 7; // 1-12
        solar.solarDay = 28;
        Lunar lunar = LunarSolarConverter.SolarToLunar(solar);
        System.out.println(lunar.lunarYear + "-" + lunar.lunarMonth + "-" + lunar.lunarDay);
    }

    @Test
    public void testFromLunarToSolar() {
        Lunar lunar = new Lunar();
        lunar.lunarYear = 2017;
        lunar.lunarMonth = 1;
        lunar.lunarDay = 1;
        Solar solar = LunarSolarConverter.LunarToSolar(lunar);
        System.out.println(solar.solarYear + "-" + solar.solarMonth + "-" + solar.solarDay);
    }
}

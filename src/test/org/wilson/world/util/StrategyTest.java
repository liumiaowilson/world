package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.json.JSONArray;

public class StrategyTest {

    @Test
    public void test() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("strategy.json");
        String content = IOUtils.toString(is);
        JSONArray.fromObject(content);
    }

}

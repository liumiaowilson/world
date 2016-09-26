package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.json.JSONObject;

public class JSONUtilsTest {

    @Test
    public void test() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("dress.json");
        String content = IOUtils.toString(is);
        JSONObject obj = JSONObject.fromObject(content);
        System.out.println(obj);
    }

}

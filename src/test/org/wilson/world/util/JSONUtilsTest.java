package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.json.JSONArray;

public class JSONUtilsTest {

    @Test
    public void test() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("micro_expression.json");
        String content = IOUtils.toString(is);
        JSONArray obj = JSONArray.fromObject(content);
        System.out.println(obj);
    }

}

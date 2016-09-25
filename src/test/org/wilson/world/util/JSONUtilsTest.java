package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.json.JSONArray;

public class JSONUtilsTest {

    @Test
    public void test() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("writing_skill.json");
        String content = IOUtils.toString(is);
        JSONArray array = JSONArray.fromObject(content);
        Object obj = JSONUtils.convert(array);
        System.out.println(obj);
    }

}

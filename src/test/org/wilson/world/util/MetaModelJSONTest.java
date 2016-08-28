package org.wilson.world.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.wilson.world.model.MetaModel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MetaModelJSONTest {

    @Test
    public void test() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("meta_model.json");
        String json = IOUtils.toString(in);
        JSONArray array = JSONArray.fromObject(json);
        for(int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            MetaModel model = new MetaModel();
            model.id = i + 1;
            model.name = obj.getString("name");
            model.definition = obj.getString("definition");
            JSONArray exampleArray = obj.getJSONArray("examples");
            for(int j = 0; j < exampleArray.size(); j++) {
                JSONObject exampleObj = exampleArray.getJSONObject(j);
                model.examples.put(exampleObj.getString("a"), exampleObj.getString("b"));
            }
        }
    }

}

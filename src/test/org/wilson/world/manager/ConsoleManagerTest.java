package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.QueryResult;
import org.wilson.world.model.QueryRow;

public class ConsoleManagerTest {

    @Test
    public void testRun() {
        String result = ConsoleManager.getInstance().run("ls");
        System.out.println(result);
    }

    @Test
    public void testEnvironment() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for(String key : keys) {
            System.out.println(key + " -> " + env.get(key));
        }
    }
    
    @Test
    public void testExecute() {
        QueryResult result = new QueryResult();
        result.rows = new ArrayList<QueryRow>();
        QueryRow row = new QueryRow();
        row.items = new ArrayList<String>();
        row.items.add("a");
        row.items.add("b");
        result.heading = row;
        
        row = new QueryRow();
        row.items = new ArrayList<String>();
        row.items.add("c");
        row.items.add("d");
        result.rows.add(row);
        
        row = new QueryRow();
        row.items = new ArrayList<String>();
        row.items.add("e");
        row.items.add("f");
        result.rows.add(row);
        
        APIResult r = APIResultUtils.buildOKAPIResult("ok");
        r.data = result;
        Response resp = APIResultUtils.buildJSONResponse(r);
        System.out.println(resp.getEntity());
    }
}

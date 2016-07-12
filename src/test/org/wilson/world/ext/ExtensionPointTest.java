package org.wilson.world.ext;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ExtensionPoint;

public class ExtensionPointTest {

    @Test
    public void testJSON() {
        ExtensionPoint ep = new ExtensionPoint();
        ep.name = "Test";
        ep.params.put("name", String.class);
        ep.params.put("age", int.class);
        ep.returnType = String.class;
        
        APIResult result = APIResultUtils.buildOKAPIResult("Success");
        result.data = ep;
        Response resp = APIResultUtils.buildJSONResponse(result);
        System.out.println(resp.getEntity());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExtensionPoints() {
        ExtensionPoint ep = new ExtensionPoint();
        ep.name = "Test";
        ep.params.put("name", String.class);
        ep.params.put("age", int.class);
        ep.returnType = String.class;
        
        APIResult result = APIResultUtils.buildOKAPIResult("Success");
        result.list = new ArrayList<ExtensionPoint>();
        result.list.add(ep);
        Response resp = APIResultUtils.buildJSONResponse(result);
        System.out.println(resp.getEntity());
    }
}

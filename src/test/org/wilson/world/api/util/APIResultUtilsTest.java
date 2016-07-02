package org.wilson.world.api.util;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.wilson.world.model.APIResult;

public class APIResultUtilsTest {

    @Test
    public void toJSON() {
        APIResult result = APIResultUtils.buildErrorAPIResult("testError");
        Response response = APIResultUtils.buildJSONResponse(result);
        System.out.println(response.getEntity());
    }

}

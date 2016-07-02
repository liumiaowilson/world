package org.wilson.world.api.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class APIResultUtils {
    private static APIResult ERROR;
    
    static {
        ERROR = new APIResult();
        ERROR.status = APIResultStatus.ERROR;
    }
    
    public static APIResult buildErrorAPIResult(String message) {
        ERROR.message = message;
        return ERROR;
    }
    
    public static Response buildJSONResponse(APIResult result) {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.alias("result", APIResult.class);
        String xml = xstream.toXML(result);
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(xml).build();
    }
}

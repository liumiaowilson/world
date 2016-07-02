package org.wilson.world.api.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;

import com.thoughtworks.xstream.XStream;

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
        XStream xstream = new XStream();
        String xml = xstream.toXML(result);
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(xml).build();
    }
}

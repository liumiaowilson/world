package org.wilson.world.api.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wilson.world.model.APIResult;
import org.wilson.world.model.APIResultStatus;
import org.wilson.world.model.DataItem;
import org.wilson.world.model.Idea;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class APIResultUtils {
    private static APIResult ERROR;
    private static APIResult OK;
    
    static {
        ERROR = new APIResult();
        ERROR.status = APIResultStatus.ERROR;
        
        OK = new APIResult();
        OK.status = APIResultStatus.OK;
    }
    
    public static APIResult buildOKAPIResult(String message) {
        OK.message = message;
        OK.data = null;
        OK.list = null;
        return OK;
    }
    
    public static APIResult buildErrorAPIResult(String message) {
        ERROR.message = message;
        ERROR.data = null;
        ERROR.list = null;
        return ERROR;
    }
    
    public static Response buildJSONResponse(APIResult result) {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.alias("result", APIResult.class);
        xstream.alias("idea", Idea.class);
        xstream.alias("dataitem", DataItem.class);
        String xml = xstream.toXML(result);
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(xml).build();
    }
}

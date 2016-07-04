package org.wilson.world.api;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.manager.ItemManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("item")
public class Item {
    private static final Logger logger = Logger.getLogger(Item.class);
    
    @GET
    @Path("/list_table_names")
    @Produces("application/json")
    public Response listTableNames(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        List<String> names = ItemManager.getInstance().getItemTableNames();
        APIResult result = APIResultUtils.buildOKAPIResult("Item table names are successfully fetched.");
        result.list = names;
        
        return APIResultUtils.buildJSONResponse(result);
    }
    
    @GET
    @Path("/clear_table")
    @Produces("application/json")
    public Response clearTable(
            @QueryParam("name") String name,
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Table name is needed."));
        }
        
        try {
            ItemManager.getInstance().clearTable(name);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Table has been successfully cleared."));
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to clear table."));
        }
    }
    
    @GET
    @Path("/clear_tables")
    @Produces("application/json")
    public Response clearTables(
            @QueryParam("names") String names,
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        if(StringUtils.isBlank(names)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Table names are needed."));
        }
        
        String [] items = names.split(",");
        
        try {
            ItemManager.getInstance().clearTables(Arrays.asList(items));
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Tables have been successfully cleared."));
        }
        catch(Exception e) {
            logger.error("failed to clear tables!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to clear tables."));
        }
    }
}

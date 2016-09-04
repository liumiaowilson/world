package org.wilson.world.api;

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
import org.wilson.world.manager.MenuManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.menu.MenuItemRole;
import org.wilson.world.model.APIResult;

@Path("/menu")
public class MenuAPI {
    private static final Logger logger = Logger.getLogger(MenuAPI.class);
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
            @QueryParam("id") String id,
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
        
        try {
            if(StringUtils.isBlank(id)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Menu id should be provided."));
            }
            
            List<String> matchingIds = MenuManager.getInstance().getMatchingMenuIds(id);
            if(matchingIds.isEmpty()) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such menu item found."));
            }
            
            MenuItem item = MenuManager.getInstance().getMenuItem(matchingIds.get(0));
            if(item != null) {
                if(MenuItemRole.Menu != item.role) {
                    if(StringUtils.isBlank(id)) {
                        return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Only menus can be fetched."));
                    }
                }
                
                String link = item.link;
                if(link.startsWith("javascript:")) {
                    link = link.substring(11);
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Item has been successfully fetched.");
                result.data = link;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Item does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get item", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

package org.wilson.world.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ProxyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Proxy;

@Path("proxy")
public class ProxyAPI {
    private static final Logger logger = Logger.getLogger(ProxyAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("host") String host,
            @FormParam("port") int port,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(host)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy host should be provided."));
        }
        host = host.trim();
        
        try {
            Proxy proxy = new Proxy();
            proxy.name = name;
            proxy.description = description;
            proxy.host = host;
            proxy.port = port;
            ProxyManager.getInstance().createProxy(proxy);
            
            Event event = new Event();
            event.type = EventType.CreateProxy;
            event.data.put("data", proxy);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Proxy has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create proxy", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("host") String host,
            @FormParam("port") int port,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(host)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy host should be provided."));
        }
        host = host.trim();
        
        try {
            Proxy oldProxy = ProxyManager.getInstance().getProxy(id);
            
            Proxy proxy = new Proxy();
            proxy.id = id;
            proxy.name = name;
            proxy.description = description;
            proxy.host = host;
            proxy.port = port;
            ProxyManager.getInstance().updateProxy(proxy);
            
            Event event = new Event();
            event.type = EventType.UpdateProxy;
            event.data.put("old_data", oldProxy);
            event.data.put("new_data", proxy);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Proxy has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update proxy", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get(
            @QueryParam("id") int id,
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
            Proxy proxy = ProxyManager.getInstance().getProxy(id);
            if(proxy != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Proxy has been successfully fetched.");
                result.data = proxy;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Proxy does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get proxy", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list(
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
            List<Proxy> proxies = ProxyManager.getInstance().getProxies();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Proxies have been successfully fetched.");
            result.list = proxies;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get proxies", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/delete")
    @Produces("application/json")
    public Response delete(
            @QueryParam("id") int id,
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
            Proxy proxy = ProxyManager.getInstance().getProxy(id);
            
            ProxyManager.getInstance().deleteProxy(id);
            
            Event event = new Event();
            event.type = EventType.DeleteProxy;
            event.data.put("data", proxy);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Proxy has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete proxy", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

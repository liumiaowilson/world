package org.wilson.world.api;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.endpoint.EndPoint;
import org.wilson.world.manager.EndPointManager;
import org.wilson.world.manager.SecManager;

@Path("endpoint")
public class EndPointAPI {
    private static final Logger logger = Logger.getLogger(EndPointAPI.class);
    
    @POST
    @Path("/{name}/{method}")
    @Produces("application/json")
    public Response doPost(
            @PathParam("name") String name, 
            @PathParam("method") String method,
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
        	EndPoint ep = EndPointManager.getInstance().getEndPoint(name);
            if(ep == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No end point with name [" + name + "] is found."));
            }
            
            Response r =  ep.doPost(method, headers, request, uriInfo);
            if(r == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("POST is not supported for [" + name + "/" + method + "]."));
            }
            
            return r;
        }
        catch(Exception e) {
            logger.error(e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/{name}/{method}")
    @Produces("application/json")
    public Response doGet(
            @PathParam("name") String name, 
            @PathParam("method") String method,
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
            EndPoint ep = EndPointManager.getInstance().getEndPoint(name);
            if(ep == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No end point with name [" + name + "] is found."));
            }
            
            Response r =  ep.doGet(method, headers, request, uriInfo);
            if(r == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("GET is not supported for [" + name + "/" + method + "]."));
            }
            
            return r;
        }
        catch(Exception e) {
            logger.error(e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/public/{name}/{method}")
    @Produces("application/json")
    public Response doPostPublic(
            @PathParam("name") String name,
            @PathParam("method") String method,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        
        try {
        	EndPoint ep = EndPointManager.getInstance().getEndPoint(name);
            if(ep == null) {
            	return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No end point with name [" + name + "] is found.");
            }
            
            Response r =  ep.doPostPublic(method, headers, request, uriInfo);
            if(r == null) {
            	return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Public POST is not supported for [" + name + "/" + method + "].");
            }
            
            return r;
        }
        catch(Exception e) {
            logger.error(e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @GET
    @Path("/public/{name}/{method}")
    @Produces("application/json")
    public Response doGetPublic(
            @PathParam("name") String name,
            @PathParam("method") String method,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        
        try {
        	EndPoint ep = EndPointManager.getInstance().getEndPoint(name);
            if(ep == null) {
            	return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No end point with name [" + name + "] is found.");
            }
            
            Response r =  ep.doGetPublic(method, headers, request, uriInfo);
            if(r == null) {
            	return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Public GET is not supported for [" + name + "/" + method + "].");
            }
            
            return r;
        }
        catch(Exception e) {
            logger.error(e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}

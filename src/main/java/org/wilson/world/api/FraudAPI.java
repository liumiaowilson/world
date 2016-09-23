package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.FraudManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Fraud;

@Path("fraud")
public class FraudAPI {
    private static final Logger logger = Logger.getLogger(FraudAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud content should be provided."));
        }
        content = content.trim();
        
        try {
            Fraud fraud = new Fraud();
            fraud.name = name;
            fraud.content = content;
            FraudManager.getInstance().createFraud(fraud);
            
            Event event = new Event();
            event.type = EventType.CreateFraud;
            event.data.put("data", fraud);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Fraud has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create fraud", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("content") String content,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud content should be provided."));
        }
        content = content.trim();
        
        try {
            Fraud oldFraud = FraudManager.getInstance().getFraud(id);
            
            Fraud fraud = new Fraud();
            fraud.id = id;
            fraud.name = name;
            fraud.content = content;
            FraudManager.getInstance().updateFraud(fraud);
            
            Event event = new Event();
            event.type = EventType.UpdateFraud;
            event.data.put("old_data", oldFraud);
            event.data.put("new_data", fraud);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Fraud has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update fraud", e);
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
            Fraud fraud = FraudManager.getInstance().getFraud(id);
            if(fraud != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Fraud has been successfully fetched.");
                result.data = fraud;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get fraud", e);
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
            List<Fraud> frauds = FraudManager.getInstance().getFrauds();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Frauds have been successfully fetched.");
            result.list = frauds;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get frauds", e);
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
            Fraud fraud = FraudManager.getInstance().getFraud(id);
            
            FraudManager.getInstance().deleteFraud(id);
            
            Event event = new Event();
            event.type = EventType.DeleteFraud;
            event.data.put("data", fraud);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Fraud has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete fraud", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/random")
    @Produces("application/json")
    public Response random(
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
            Fraud fraud = FraudManager.getInstance().randomFraud();
            if(fraud != null) {
                fraud = FraudManager.getInstance().getFraud(fraud.id, false);
                if(fraud.content == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Fraud is not loaded yet."));
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Random fraud has been successfully fetched.");
                
                String content = fraud.content;
                content = content.replaceAll("\n", "<br/>");
                result.data = content;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random fraud does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random fraud!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/view_public")
    @Produces("application/json")
    public Response viewPublic(
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            Fraud fraud = FraudManager.getInstance().randomFraud();
            if(fraud == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No fraud is found");
            }
            
            fraud = FraudManager.getInstance().getFraud(fraud.id, false);
            String content = fraud.content;
            content = content.replaceAll("\n", "<br/>");
            
            request.getSession().setAttribute("world-public-fraud", content);
            
            return APIResultUtils.buildURLResponse(request, "public/view_fraud.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view fraud", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}

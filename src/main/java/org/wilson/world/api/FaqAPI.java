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
import org.wilson.world.manager.FaqManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Faq;

@Path("faq")
public class FaqAPI {
    private static final Logger logger = Logger.getLogger(FaqAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("question") String question,
            @FormParam("answer") String answer,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(question)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq question should be provided."));
        }
        question = question.trim();
        if(StringUtils.isBlank(answer)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq answer should be provided."));
        }
        answer = answer.trim();
        
        try {
            Faq faq = new Faq();
            faq.name = name;
            faq.question = question;
            faq.answer = answer;
            FaqManager.getInstance().createFaq(faq);
            
            Event event = new Event();
            event.type = EventType.CreateFaq;
            event.data.put("data", faq);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Faq has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create faq", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("question") String question,
            @FormParam("answer") String answer,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(question)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq question should be provided."));
        }
        question = question.trim();
        if(StringUtils.isBlank(answer)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq answer should be provided."));
        }
        answer = answer.trim();
        
        try {
            Faq oldFaq = FaqManager.getInstance().getFaq(id);
            
            Faq faq = new Faq();
            faq.id = id;
            faq.name = name;
            faq.question = question;
            faq.answer = answer;
            FaqManager.getInstance().updateFaq(faq);
            
            Event event = new Event();
            event.type = EventType.UpdateFaq;
            event.data.put("old_data", oldFaq);
            event.data.put("new_data", faq);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Faq has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update faq", e);
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
            Faq faq = FaqManager.getInstance().getFaq(id);
            if(faq != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Faq has been successfully fetched.");
                result.data = faq;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Faq does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get faq", e);
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
            List<Faq> faqs = FaqManager.getInstance().getFaqs();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Faqs have been successfully fetched.");
            result.list = faqs;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get faqs", e);
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
            Faq faq = FaqManager.getInstance().getFaq(id);
            
            FaqManager.getInstance().deleteFaq(id);
            
            Event event = new Event();
            event.type = EventType.DeleteFaq;
            event.data.put("data", faq);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Faq has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete faq", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

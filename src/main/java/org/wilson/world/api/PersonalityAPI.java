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
import org.wilson.world.manager.PersonalityManager;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Personality;
import org.wilson.world.personality.PersonalityQuiz;

@Path("personality")
public class PersonalityAPI {
    private static final Logger logger = Logger.getLogger(PersonalityAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("tags") String tags,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(tags)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality tags should be provided."));
        }
        tags = tags.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality description should be provided."));
        }
        description = description.trim();
        
        try {
            Personality personality = new Personality();
            personality.name = name;
            personality.tags = tags;
            personality.description = description;
            PersonalityManager.getInstance().createPersonality(personality);
            
            Event event = new Event();
            event.type = EventType.CreatePersonality;
            event.data.put("data", personality);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Personality has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create personality", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("tags") String tags,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(tags)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality tags should be provided."));
        }
        tags = tags.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality description should be provided."));
        }
        description = description.trim();
        
        try {
            Personality oldPersonality = PersonalityManager.getInstance().getPersonality(id);
            
            Personality personality = new Personality();
            personality.id = id;
            personality.name = name;
            personality.tags = tags;
            personality.description = description;
            PersonalityManager.getInstance().updatePersonality(personality);
            
            Event event = new Event();
            event.type = EventType.UpdatePersonality;
            event.data.put("old_data", oldPersonality);
            event.data.put("new_data", personality);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Personality has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update personality", e);
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
            Personality personality = PersonalityManager.getInstance().getPersonality(id);
            if(personality != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Personality has been successfully fetched.");
                result.data = personality;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Personality does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get personality", e);
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
            List<Personality> personalities = PersonalityManager.getInstance().getPersonalities();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Personalities have been successfully fetched.");
            result.list = personalities;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get personalities", e);
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
            Personality personality = PersonalityManager.getInstance().getPersonality(id);
            
            PersonalityManager.getInstance().deletePersonality(id);
            
            Event event = new Event();
            event.type = EventType.DeletePersonality;
            event.data.put("data", personality);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Personality has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete personality", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/do_quiz")
    @Produces("application/json")
    public Response doQuiz(
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
            PersonalityQuiz quiz = (PersonalityQuiz) QuizDataManager.getInstance().getQuizOfClass(PersonalityQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doPersonalityQuiz()");
            
            APIResult result = APIResultUtils.buildOKAPIResult("Quiz has been successfully fetched.");
            result.data = quiz.getId();
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to do quiz", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

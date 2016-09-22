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
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.ReactionManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Reaction;
import org.wilson.world.reaction.ReactionQuiz;

@Path("reaction")
public class ReactionAPI {
    private static final Logger logger = Logger.getLogger(ReactionAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("condition") String condition,
            @FormParam("result") String result,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(condition)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction condition should be provided."));
        }
        condition = condition.trim();
        if(StringUtils.isBlank(result)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction result should be provided."));
        }
        result = result.trim();
        
        try {
            Reaction reaction = new Reaction();
            reaction.name = name;
            reaction.condition = condition;
            reaction.result = result;
            ReactionManager.getInstance().createReaction(reaction);
            
            Event event = new Event();
            event.type = EventType.CreateReaction;
            event.data.put("data", reaction);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reaction has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create reaction", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("condition") String condition,
            @FormParam("result") String result,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(condition)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction condition should be provided."));
        }
        condition = condition.trim();
        if(StringUtils.isBlank(result)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction result should be provided."));
        }
        result = result.trim();
        
        try {
            Reaction oldReaction = ReactionManager.getInstance().getReaction(id);
            
            Reaction reaction = new Reaction();
            reaction.id = id;
            reaction.name = name;
            reaction.condition = condition;
            reaction.result = result;
            ReactionManager.getInstance().updateReaction(reaction);
            
            Event event = new Event();
            event.type = EventType.UpdateReaction;
            event.data.put("old_data", oldReaction);
            event.data.put("new_data", reaction);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reaction has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update reaction", e);
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
            Reaction reaction = ReactionManager.getInstance().getReaction(id);
            if(reaction != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Reaction has been successfully fetched.");
                result.data = reaction;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Reaction does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get reaction", e);
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
            List<Reaction> reactions = ReactionManager.getInstance().getReactions();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Reactions have been successfully fetched.");
            result.list = reactions;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get reactions", e);
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
            Reaction reaction = ReactionManager.getInstance().getReaction(id);
            
            ReactionManager.getInstance().deleteReaction(id);
            
            Event event = new Event();
            event.type = EventType.DeleteReaction;
            event.data.put("data", reaction);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Reaction has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete reaction", e);
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
            ReactionQuiz quiz = (ReactionQuiz) QuizDataManager.getInstance().getQuizOfClass(ReactionQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doReactionQuiz()");
            
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

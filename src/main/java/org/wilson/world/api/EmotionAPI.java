package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.wilson.world.emotion.EmotionQuiz;
import org.wilson.world.emotion.EmotionType;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EmotionManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Emotion;

@Path("emotion")
public class EmotionAPI {
    private static final Logger logger = Logger.getLogger(EmotionAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("ecstacy") int ecstacy,
            @FormParam("grief") int grief,
            @FormParam("admiration") int admiration,
            @FormParam("loathing") int loathing,
            @FormParam("rage") int rage,
            @FormParam("terror") int terror,
            @FormParam("vigilance") int vigilance,
            @FormParam("amazement") int amazement,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Emotion name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Emotion description should be provided."));
        }
        description = description.trim();
        
        try {
            Emotion emotion = new Emotion();
            emotion.name = name;
            emotion.description = description;
            emotion.ecstacy = ecstacy;
            emotion.grief = grief;
            emotion.admiration = admiration;
            emotion.loathing = loathing;
            emotion.rage = rage;
            emotion.terror = terror;
            emotion.vigilance = vigilance;
            emotion.amazement = amazement;
            EmotionManager.getInstance().createEmotion(emotion);
            
            Event event = new Event();
            event.type = EventType.CreateEmotion;
            event.data.put("data", emotion);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Emotion has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create emotion", e);
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
            @FormParam("ecstacy") int ecstacy,
            @FormParam("grief") int grief,
            @FormParam("admiration") int admiration,
            @FormParam("loathing") int loathing,
            @FormParam("rage") int rage,
            @FormParam("terror") int terror,
            @FormParam("vigilance") int vigilance,
            @FormParam("amazement") int amazement,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Emotion name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Emotion description should be provided."));
        }
        description = description.trim();
        
        try {
            Emotion oldEmotion = EmotionManager.getInstance().getEmotion(id);
            
            Emotion emotion = new Emotion();
            emotion.id = id;
            emotion.name = name;
            emotion.description = description;
            emotion.ecstacy = ecstacy;
            emotion.grief = grief;
            emotion.admiration = admiration;
            emotion.loathing = loathing;
            emotion.rage = rage;
            emotion.terror = terror;
            emotion.vigilance = vigilance;
            emotion.amazement = amazement;
            EmotionManager.getInstance().updateEmotion(emotion);
            
            Event event = new Event();
            event.type = EventType.UpdateEmotion;
            event.data.put("old_data", oldEmotion);
            event.data.put("new_data", emotion);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Emotion has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update emotion", e);
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
            Emotion emotion = EmotionManager.getInstance().getEmotion(id);
            if(emotion != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Emotion has been successfully fetched.");
                result.data = emotion;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Emotion does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get emotion", e);
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
            List<Emotion> emotions = EmotionManager.getInstance().getEmotions();
            Collections.sort(emotions, new Comparator<Emotion>(){

				@Override
				public int compare(Emotion o1, Emotion o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Emotions have been successfully fetched.");
            result.list = emotions;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get emotions", e);
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
            Emotion emotion = EmotionManager.getInstance().getEmotion(id);
            
            EmotionManager.getInstance().deleteEmotion(id);
            
            Event event = new Event();
            event.type = EventType.DeleteEmotion;
            event.data.put("data", emotion);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Emotion has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete emotion", e);
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
            EmotionQuiz quiz = (EmotionQuiz) QuizDataManager.getInstance().getQuizOfClass(EmotionQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doEmotionQuiz()");
            
            APIResult result = APIResultUtils.buildOKAPIResult("Quiz has been successfully fetched.");
            result.data = quiz.getId();
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to do quiz", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/create_public")
    @Produces("application/json")
    public Response createPublic(
            @FormParam("key") String key,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("ecstacy") int ecstacy,
            @FormParam("grief") int grief,
            @FormParam("admiration") int admiration,
            @FormParam("loathing") int loathing,
            @FormParam("rage") int rage,
            @FormParam("terror") int terror,
            @FormParam("vigilance") int vigilance,
            @FormParam("amazement") int amazement,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Name should be provided.");
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Description should be provided.");
        }
        description = description.trim();
        
        try {
            if(name.length() > 50) {
                name = name.substring(0, 50);
            }
            if(description.length() > 200) {
                description = description.substring(0, 200);
            }
            
            Emotion emotion = new Emotion();
            emotion.name = name;
            emotion.description = description;
            emotion.ecstacy = ecstacy;
            emotion.grief = grief;
            emotion.admiration = admiration;
            emotion.loathing = loathing;
            emotion.rage = rage;
            emotion.terror = terror;
            emotion.vigilance = vigilance;
            emotion.amazement = amazement;
            EmotionManager.getInstance().createEmotion(emotion);
            
            Event event = new Event();
            event.type = EventType.CreateEmotion;
            event.data.put("data", emotion);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildURLResponse(request, "public/emotion.jsp");
        }
        catch(Exception e) {
            logger.error("failed to create emotion", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @GET
    @Path("/comparision")
    @Produces("application/json")
    public Response compare(
            @QueryParam("types") String types,
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
        
        if(StringUtils.isBlank(types)) {
            types = "";
        }
        types = types.trim();
        
        try {
            List<EmotionType> typeList = new ArrayList<EmotionType>();
            for(String type : types.split(",")) {
                if(!StringUtils.isBlank(type)) {
                    typeList.add(EmotionType.valueOf(type));
                }
            }
            
            List<Emotion> emotions = EmotionManager.getInstance().getEmotions(typeList);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Emotions have been successfully fetched.");
            result.list = emotions;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get emotions", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

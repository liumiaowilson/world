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
import org.wilson.world.manager.FlashCardManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.FlashCard;

@Path("flashcard")
public class FlashCardAPI {
    private static final Logger logger = Logger.getLogger(FlashCardAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("setId") int setId,
            @FormParam("top") String top, 
            @FormParam("bottom") String bottom, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(top)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard top should be provided."));
        }
        top = top.trim();
        if(StringUtils.isBlank(bottom)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard bottom should be provided."));
        }
        bottom = bottom.trim();
        
        try {
            FlashCard card = new FlashCard();
            card.name = name;
            card.setId = setId;
            card.top = top;
            card.bottom = bottom;
            FlashCardManager.getInstance().createFlashCard(card);
            
            Event event = new Event();
            event.type = EventType.CreateFlashCard;
            event.data.put("data", card);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Card has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create card", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("setId") int setId,
            @FormParam("top") String top, 
            @FormParam("bottom") String bottom, 
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(top)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard top should be provided."));
        }
        top = top.trim();
        if(StringUtils.isBlank(bottom)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Flashcard bottom should be provided."));
        }
        bottom = bottom.trim();
        
        try {
            FlashCard oldCard = FlashCardManager.getInstance().getFlashCard(id);
            
            FlashCard card = new FlashCard();
            card.id = id;
            card.name = name;
            card.setId = setId;
            card.top = top;
            card.bottom = bottom;
            FlashCardManager.getInstance().updateFlashCard(card);
            
            Event event = new Event();
            event.type = EventType.UpdateFlashCard;
            event.data.put("old_data", oldCard);
            event.data.put("new_data", card);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Card has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update card", e);
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
            FlashCard card = FlashCardManager.getInstance().getFlashCard(id);
            if(card != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Card has been successfully fetched.");
                result.data = card;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Card does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get card", e);
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
            List<FlashCard> cards = FlashCardManager.getInstance().getFlashCards();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Cards have been successfully fetched.");
            result.list = cards;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get cards", e);
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
            FlashCard card = FlashCardManager.getInstance().getFlashCard(id);
            
            FlashCardManager.getInstance().deleteFlashCard(id);
            
            Event event = new Event();
            event.type = EventType.DeleteFlashCard;
            event.data.put("data", card);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Card has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete card", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.QuestDefManager;
import org.wilson.world.manager.QuestManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Quest;
import org.wilson.world.model.QuestDef;

@Path("quest_def")
public class QuestDefAPI {
    private static final Logger logger = Logger.getLogger(QuestDefAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
            @FormParam("pay") int pay,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Quest def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Quest def content should be provided."));
        }
        content = content.trim();
        
        try {
            QuestDef def = new QuestDef();
            def.name = name;
            def.content = content;
            def.pay = pay;
            QuestDefManager.getInstance().createQuestDef(def);
            
            Event event = new Event();
            event.type = EventType.CreateQuestDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Quest def has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create quest def", e);
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
            @FormParam("pay") int pay,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Quest def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Quest def content should be provided."));
        }
        content = content.trim();
        
        try {
            QuestDef oldDef = QuestDefManager.getInstance().getQuestDef(id);
            
            QuestDef def = new QuestDef();
            def.id = id;
            def.name = name;
            def.content = content;
            def.pay = pay;
            QuestDefManager.getInstance().updateQuestDef(def);
            
            Event event = new Event();
            event.type = EventType.UpdateQuestDef;
            event.data.put("old_data", oldDef);
            event.data.put("new_data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Quest def has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update quest def", e);
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
            QuestDef def = QuestDefManager.getInstance().getQuestDef(id);
            if(def != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Quest def has been successfully fetched.");
                result.data = def;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Quest def does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get quest def", e);
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
            List<QuestDef> defs = QuestDefManager.getInstance().getQuestDefs();
            
            Collections.sort(defs, new Comparator<QuestDef>(){

                @Override
                public int compare(QuestDef d1, QuestDef d2) {
                    return d1.name.compareTo(d2.name);
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Quest defs have been successfully fetched.");
            result.list = defs;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get quest defs", e);
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
            QuestDef def = QuestDefManager.getInstance().getQuestDef(id);
            
            QuestDefManager.getInstance().deleteQuestDef(id);
            
            Event event = new Event();
            event.type = EventType.DeleteQuestDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Quest def has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete quest def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/achieve_public")
    @Produces("application/json")
    public Response achievePublic(
            @FormParam("key") String key,
            @FormParam("id") int id,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            String ret = QuestDefManager.getInstance().achieveQuestDef(id);
            if(ret == null) {
                Quest quest = QuestManager.getInstance().getLastCreatedQuest();
                
                Event event = new Event();
                event.type = EventType.CreateQuest;
                event.data.put("data", quest);
                EventManager.getInstance().fireEvent(event);
                
                return APIResultUtils.buildURLResponse(request, "quest.jsp");
            }
            else {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", ret);
            }
        }
        catch(Exception e) {
            logger.error("failed to achieve quest", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}

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
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.ScenarioManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Scenario;

@Path("scenario")
public class ScenarioAPI {
    private static final Logger logger = Logger.getLogger(ScenarioAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("stimuli") String stimuli,
            @FormParam("reaction") String reaction,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(stimuli)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario stimuli should be provided."));
        }
        stimuli = stimuli.trim();
        if(StringUtils.isBlank(reaction)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario reaction should be provided."));
        }
        reaction = reaction.trim();
        
        try {
            Scenario scenario = new Scenario();
            scenario.name = name;
            scenario.stimuli = stimuli;
            scenario.reaction = reaction;
            ScenarioManager.getInstance().createScenario(scenario);
            
            Event event = new Event();
            event.type = EventType.CreateScenario;
            event.data.put("data", scenario);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Scenario has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create scenario", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("stimuli") String stimuli,
            @FormParam("reaction") String reaction,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(stimuli)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario stimuli should be provided."));
        }
        stimuli = stimuli.trim();
        if(StringUtils.isBlank(reaction)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario reaction should be provided."));
        }
        reaction = reaction.trim();
        
        try {
            Scenario oldScenario = ScenarioManager.getInstance().getScenario(id);
            
            Scenario scenario = new Scenario();
            scenario.id = id;
            scenario.name = name;
            scenario.stimuli = stimuli;
            scenario.reaction = reaction;
            ScenarioManager.getInstance().updateScenario(scenario);
            
            Event event = new Event();
            event.type = EventType.UpdateScenario;
            event.data.put("old_data", oldScenario);
            event.data.put("new_data", scenario);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Scenario has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update scenario", e);
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
            Scenario scenario = ScenarioManager.getInstance().getScenario(id);
            if(scenario != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Scenario has been successfully fetched.");
                result.data = scenario;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Scenario does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get scenario", e);
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
            List<Scenario> scenarios = ScenarioManager.getInstance().getScenarios();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Scenarios have been successfully fetched.");
            result.list = scenarios;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get scenarios", e);
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
            Scenario scenario = ScenarioManager.getInstance().getScenario(id);
            
            ScenarioManager.getInstance().deleteScenario(id);
            
            Event event = new Event();
            event.type = EventType.DeleteScenario;
            event.data.put("data", scenario);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Scenario has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete scenario", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/read")
    @Produces("application/json")
    public Response read(
            @FormParam("id") int id, 
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
            ScenarioManager.getInstance().read(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Read process has been successfully started."));
        }
        catch(Exception e) {
            logger.error("failed to start read process", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/revive")
    @Produces("application/json")
    public Response revive(
            @FormParam("id") int id, 
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
        
        try {
            ScenarioManager.getInstance().revive(id, description);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Revive process has been successfully started."));
        }
        catch(Exception e) {
            logger.error("failed to start revive process", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/react")
    @Produces("application/json")
    public Response react(
            @FormParam("id") int id, 
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
        
        try {
            ScenarioManager.getInstance().react(id, description);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("React process has been successfully started."));
        }
        catch(Exception e) {
            logger.error("failed to start react process", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/recap")
    @Produces("application/json")
    public Response recap(
            @FormParam("id") int id, 
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
        
        try {
            String reviveMessage = ScenarioManager.getInstance().getReviveMessage();
            String reactMessage = ScenarioManager.getInstance().getReactMessage();
            
            ScenarioManager.getInstance().recap(id, description);
            
            Scenario scenario = ScenarioManager.getInstance().getScenario(id);
            if(scenario != null) {
                Event event = new Event();
                event.type = EventType.TrainScenario;
                event.data.put("data", scenario);
                EventManager.getInstance().fireEvent(event);
            }
            
            if(!StringUtils.isBlank(reviveMessage)) {
                if(DiceManager.getInstance().dice(reviveMessage.length())) {
                    int exp = ExpManager.getInstance().getExp();
                    exp = exp + 1;
                    ExpManager.getInstance().setExp(exp);
                    
                    NotifyManager.getInstance().notifySuccess("Gained one extra experience from reviving.");
                }
            }
            
            if(!StringUtils.isBlank(reactMessage)) {
                if(DiceManager.getInstance().dice(reactMessage.length())) {
                    int exp = ExpManager.getInstance().getExp();
                    exp = exp + 1;
                    ExpManager.getInstance().setExp(exp);
                    
                    NotifyManager.getInstance().notifySuccess("Gained one extra experience from reacting.");
                }
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Recap process has been successfully started."));
        }
        catch(Exception e) {
            logger.error("failed to start recap process", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

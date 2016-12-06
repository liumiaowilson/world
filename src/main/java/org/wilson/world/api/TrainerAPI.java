package org.wilson.world.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
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
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.TrainerSkillManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TrainerSkill;

@Path("/trainer")
public class TrainerAPI {
    private static final Logger logger = Logger.getLogger(TrainerAPI.class);
    
    @GET
    @Path("/list_learn")
    @Produces("application/json")
    public Response listLearn(
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
            List<TrainerSkill> skills = TrainerSkillManager.getInstance().getSkillsToLearn();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Skills have been successfully fetched.");
            result.list = skills;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get skills", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_upgrade")
    @Produces("application/json")
    public Response listUpgrade(
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
            List<TrainerSkill> skills = TrainerSkillManager.getInstance().getSkillsToUpgrade();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Skills have been successfully fetched.");
            result.list = skills;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get skills", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/rotate")
    @Produces("application/json")
    public Response rotate(
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
        	int skillpoints = CharManager.getInstance().getSkillPoints();
        	if(skillpoints < 1) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No enough skill points."));
        	}
        	skillpoints -= 1;
        	CharManager.getInstance().setSkillPoints(skillpoints);
        	
            TrainerSkillManager.getInstance().reloadSkills();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Skills have been successfully reloaded.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to reload skills", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/learn")
    @Produces("application/json")
    public Response learn(
            @QueryParam("id") int id,
            @QueryParam("price") int price,
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
            String ret = TrainerSkillManager.getInstance().learn(id, price);
            if(ret == null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Trainer skill has been successfully learnt.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to learn skill", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/upgrade")
    @Produces("application/json")
    public Response upgrade(
            @QueryParam("id") int id,
            @QueryParam("price") int price,
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
            String ret = TrainerSkillManager.getInstance().upgrade(id, price);
            if(ret == null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Trainer skill has been successfully upgraded.");
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to upgrade skill", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

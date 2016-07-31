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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.UserSkillManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.UserSkill;

@Path("user_skill")
public class UserSkillAPI {
    private static final Logger logger = Logger.getLogger(UserSkillAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("skillId") int skillId, 
            @FormParam("level") int level, 
            @FormParam("exp") int exp, 
            @FormParam("lastTime") long lastTime, 
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
            UserSkill skill = new UserSkill();
            skill.skillId = skillId;
            skill.level = level;
            skill.exp = exp;
            skill.lastTime = lastTime;
            UserSkillManager.getInstance().createUserSkill(skill);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("User skill has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create skill", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("skillId") int skillId, 
            @FormParam("level") int level, 
            @FormParam("exp") int exp, 
            @FormParam("lastTime") long lastTime, 
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
            UserSkill skill = new UserSkill();
            skill.id = id;
            skill.skillId = skillId;
            skill.level = level;
            skill.exp = exp;
            skill.lastTime = lastTime;
            UserSkillManager.getInstance().updateUserSkill(skill);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("User skill has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update skill", e);
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
            UserSkill skill = UserSkillManager.getInstance().getUserSkill(id);
            if(skill != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("User skill has been successfully fetched.");
                result.data = skill;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("User skill does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get skill", e);
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
            List<UserSkill> skills = UserSkillManager.getInstance().getUserSkills();
            
            APIResult result = APIResultUtils.buildOKAPIResult("User skills have been successfully fetched.");
            result.list = skills;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get skills", e);
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
            UserSkillManager.getInstance().deleteUserSkill(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("User skill has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete skill", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

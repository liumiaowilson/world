package org.wilson.world.api;

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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.NovelFragmentManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.novel.NovelFragmentInfo;

@Path("novel_fragment")
public class NovelFragmentAPI {
    private static final Logger logger = Logger.getLogger(NovelFragmentAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("stageId") int stageId,
            @FormParam("condition") String condition,
            @FormParam("content") String content,
            @FormParam("preCode") String preCode,
            @FormParam("postCode") String postCode,
            @FormParam("image") String image,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelFragment name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelFragment content should be provided."));
        }
        content = content.trim();
        
        try {
        	NovelFragment fragment = new NovelFragment();
            fragment.name = name;
            fragment.stageId = stageId;
            fragment.condition = condition;
            fragment.content = content;
            fragment.preCode = preCode;
            fragment.postCode = postCode;
            fragment.image = image;
            NovelFragmentManager.getInstance().createNovelFragment(fragment);
            
            Event event = new Event();
            event.type = EventType.CreateNovelFragment;
            event.data.put("data", fragment);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelFragment has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create novel fragment", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("stageId") int stageId,
            @FormParam("condition") String condition,
            @FormParam("content") String content,
            @FormParam("preCode") String preCode,
            @FormParam("postCode") String postCode,
            @FormParam("image") String image,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelFragment name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelFragment content should be provided."));
        }
        content = content.trim();
        
        try {
        	NovelFragment oldFragment = NovelFragmentManager.getInstance().getNovelFragment(id);
            
        	NovelFragment fragment = new NovelFragment();
            fragment.id = id;
            fragment.name = name;
            fragment.stageId = stageId;
            fragment.condition = condition;
            fragment.content = content;
            fragment.preCode = preCode;
            fragment.postCode = postCode;
            fragment.image = image;
            NovelFragmentManager.getInstance().updateNovelFragment(fragment);
            
            Event event = new Event();
            event.type = EventType.UpdateNovelFragment;
            event.data.put("old_data", oldFragment);
            event.data.put("new_data", fragment);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelFragment has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update novel fragment", e);
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
        	NovelFragment fragment = NovelFragmentManager.getInstance().getNovelFragment(id);
            if(fragment != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("NovelFragment has been successfully fetched.");
                result.data = fragment;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("NovelFragment does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel fragment", e);
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
            List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragments();
            
            Collections.sort(fragments, new Comparator<NovelFragment>(){

				@Override
				public int compare(NovelFragment o1, NovelFragment o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelFragments have been successfully fetched.");
            result.list = fragments;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel fragments", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/show")
    @Produces("application/json")
    public Response show(
    		@QueryParam("stageId") int stageId,
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
            List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragmentsOfStage(stageId);
            
            Collections.sort(fragments, new Comparator<NovelFragment>(){

				@Override
				public int compare(NovelFragment o1, NovelFragment o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelFragments have been successfully fetched.");
            result.list = fragments;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel fragments", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/query")
    @Produces("application/json")
    public Response query(
    		@FormParam("script") String script,
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
            List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragments(script);
            
            Collections.sort(fragments, new Comparator<NovelFragment>(){

				@Override
				public int compare(NovelFragment o1, NovelFragment o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelFragments have been successfully fetched.");
            result.list = fragments;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel fragments", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/validate")
    @Produces("application/json")
    public Response validate(
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
            List<NovelFragmentInfo> infos = NovelFragmentManager.getInstance().validateAll();
            
            Collections.sort(infos, new Comparator<NovelFragmentInfo>(){

				@Override
				public int compare(NovelFragmentInfo o1, NovelFragmentInfo o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelFragmentInfos have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel fragment infos", e);
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
        	NovelFragment fragment = NovelFragmentManager.getInstance().getNovelFragment(id);
            
        	NovelFragmentManager.getInstance().deleteNovelFragment(id);
            
            Event event = new Event();
            event.type = EventType.DeleteNovelFragment;
            event.data.put("data", fragment);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("NovelFragment has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete novel fragment", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/find_roles")
    @Produces("application/json")
    public Response findRoles(
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
        	NovelFragment fragment = NovelFragmentManager.getInstance().getNovelFragment(id);
        	if(fragment == null) {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such fragment could be found."));
        	}
        	
            List<NovelRole> roles = NovelFragmentManager.getInstance().getMatchedNovelRoles(fragment);
            
            Collections.sort(roles, new Comparator<NovelRole>(){

				@Override
				public int compare(NovelRole o1, NovelRole o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("NovelRoles have been successfully fetched.");
            result.list = roles;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get novel roles", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_plain")
    @Produces("application/json")
    public Response listPlain(
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
            List<NovelFragment> fragments = NovelFragmentManager.getInstance().getPlainNovelFragments();
            
            Collections.sort(fragments, new Comparator<NovelFragment>(){

				@Override
				public int compare(NovelFragment o1, NovelFragment o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Plain NovelFragments have been successfully fetched.");
            result.list = fragments;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get plain novel fragments", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

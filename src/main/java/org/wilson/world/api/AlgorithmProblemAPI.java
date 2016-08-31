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
import org.wilson.world.manager.AlgorithmProblemManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.JavaManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.AlgorithmProblem;

@Path("algorithm_problem")
public class AlgorithmProblemAPI {
    private static final Logger logger = Logger.getLogger(AlgorithmProblemAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("interfaceDef") String interfaceDef,
            @FormParam("dataset") String dataset,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(interfaceDef)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem interfaceDef should be provided."));
        }
        interfaceDef = interfaceDef.trim();
        if(StringUtils.isBlank(dataset)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem dataset should be provided."));
        }
        dataset = dataset.trim();
        
        try {
            String ret = JavaManager.getInstance().validate(interfaceDef);
            if(ret != null) {
                APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
            
            AlgorithmProblem problem = new AlgorithmProblem();
            problem.name = name;
            problem.description = description;
            problem.interfaceDef = interfaceDef;
            problem.dataset = dataset;
            AlgorithmProblemManager.getInstance().createAlgorithmProblem(problem);
            
            Event event = new Event();
            event.type = EventType.CreateAlgorithmProblem;
            event.data.put("data", problem);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("AlgorithmProblem has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create problem", e);
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
            @FormParam("interfaceDef") String interfaceDef,
            @FormParam("dataset") String dataset,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(interfaceDef)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem interfaceDef should be provided."));
        }
        interfaceDef = interfaceDef.trim();
        if(StringUtils.isBlank(dataset)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Problem dataset should be provided."));
        }
        dataset = dataset.trim();
        
        try {
            String ret = JavaManager.getInstance().validate(interfaceDef);
            if(ret != null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
            
            AlgorithmProblem oldProblem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(id);
            
            AlgorithmProblem problem = new AlgorithmProblem();
            problem.id = id;
            problem.name = name;
            problem.description = description;
            problem.interfaceDef = interfaceDef;
            problem.dataset = dataset;
            AlgorithmProblemManager.getInstance().updateAlgorithmProblem(problem);
            
            Event event = new Event();
            event.type = EventType.UpdateAlgorithmProblem;
            event.data.put("old_data", oldProblem);
            event.data.put("new_data", problem);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("AlgorithmProblem has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update problem", e);
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
            AlgorithmProblem problem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(id);
            if(problem != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("AlgorithmProblem has been successfully fetched.");
                result.data = problem;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("AlgorithmProblem does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get problem", e);
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
            List<AlgorithmProblem> problems = AlgorithmProblemManager.getInstance().getAlgorithmProblems();
            
            APIResult result = APIResultUtils.buildOKAPIResult("AlgorithmProblems have been successfully fetched.");
            result.list = problems;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get problems", e);
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
            AlgorithmProblem problem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(id);
            
            AlgorithmProblemManager.getInstance().deleteAlgorithmProblem(id);
            
            Event event = new Event();
            event.type = EventType.DeleteAlgorithmProblem;
            event.data.put("data", problem);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("AlgorithmProblem has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete problem", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

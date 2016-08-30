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
import org.wilson.world.manager.AlgorithmManager;
import org.wilson.world.manager.AlgorithmProblemManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Algorithm;
import org.wilson.world.model.AlgorithmProblem;

@Path("algorithm")
public class AlgorithmAPI {
    private static final Logger logger = Logger.getLogger(AlgorithmAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("problemId") int problemId,
            @FormParam("description") String description,
            @FormParam("impl") String impl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm impl should be provided."));
        }
        impl = impl.trim();
        
        try {
            Algorithm algorithm = new Algorithm();
            algorithm.name = name;
            algorithm.problemId = problemId;
            algorithm.description = description;
            algorithm.impl = impl;
            AlgorithmManager.getInstance().createAlgorithm(algorithm);
            
            Event event = new Event();
            event.type = EventType.CreateAlgorithm;
            event.data.put("data", algorithm);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Algorithm has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create algorithm", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("problemId") int problemId,
            @FormParam("description") String description,
            @FormParam("impl") String impl,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm impl should be provided."));
        }
        impl = impl.trim();
        
        try {
            Algorithm oldAlgorithm = AlgorithmManager.getInstance().getAlgorithm(id);
            
            Algorithm algorithm = new Algorithm();
            algorithm.id = id;
            algorithm.name = name;
            algorithm.problemId = problemId;
            algorithm.description = description;
            algorithm.impl = impl;
            AlgorithmManager.getInstance().updateAlgorithm(algorithm);
            
            Event event = new Event();
            event.type = EventType.UpdateAlgorithm;
            event.data.put("old_data", oldAlgorithm);
            event.data.put("new_data", algorithm);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Algorithm has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update algorithm", e);
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
            Algorithm algorithm = AlgorithmManager.getInstance().getAlgorithm(id);
            if(algorithm != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Algorithm has been successfully fetched.");
                result.data = algorithm;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get algorithm", e);
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
            List<Algorithm> algorithms = AlgorithmManager.getInstance().getAlgorithms();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Algorithms have been successfully fetched.");
            result.list = algorithms;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get algorithms", e);
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
            Algorithm algorithm = AlgorithmManager.getInstance().getAlgorithm(id);
            
            AlgorithmManager.getInstance().deleteAlgorithm(id);
            
            Event event = new Event();
            event.type = EventType.DeleteAlgorithm;
            event.data.put("data", algorithm);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Algorithm has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete algorithm", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_default_impl")
    @Produces("application/json")
    public Response getDefaultImpl(
            @QueryParam("problemId") int problemId,
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
            AlgorithmProblem problem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(problemId);
            if(problem != null) {
                APIResult result = APIResultUtils.buildOKAPIResult(problem.interfaceDef);
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Algorithm problem does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get problem", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

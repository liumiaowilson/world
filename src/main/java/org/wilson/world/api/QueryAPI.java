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
import org.wilson.world.manager.QueryManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Query;
import org.wilson.world.model.QueryItem;
import org.wilson.world.query.QueryProcessor;

@Path("query")
public class QueryAPI {
    private static final Logger logger = Logger.getLogger(QueryAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("impl") String impl,
            @FormParam("idExpr") String idExpr,
            @FormParam("nameExpr") String nameExpr,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query impl should be provided."));
        }
        impl = impl.trim();
        if(StringUtils.isBlank(idExpr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query id expression should be provided."));
        }
        idExpr = idExpr.trim();
        if(StringUtils.isBlank(nameExpr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query name expression should be provided."));
        }
        nameExpr = nameExpr.trim();
        
        try {
            Query query = new Query();
            query.name = name;
            query.impl = impl;
            query.idExpr = idExpr;
            query.nameExpr = nameExpr;
            QueryManager.getInstance().createQuery(query);
            
            Event event = new Event();
            event.type = EventType.CreateQuery;
            event.data.put("data", query);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Query has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create query", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("impl") String impl,
            @FormParam("idExpr") String idExpr,
            @FormParam("nameExpr") String nameExpr,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(impl)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query impl should be provided."));
        }
        impl = impl.trim();
        if(StringUtils.isBlank(idExpr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query id expression should be provided."));
        }
        idExpr = idExpr.trim();
        if(StringUtils.isBlank(nameExpr)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query name expression should be provided."));
        }
        nameExpr = nameExpr.trim();
        
        try {
            Query oldQuery = QueryManager.getInstance().getQuery(id);
            
            Query query = new Query();
            query.id = id;
            query.name = name;
            query.impl = impl;
            query.idExpr = idExpr;
            query.nameExpr = nameExpr;
            QueryManager.getInstance().updateQuery(query);
            
            Event event = new Event();
            event.type = EventType.UpdateQuery;
            event.data.put("old_data", oldQuery);
            event.data.put("new_data", query);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Query has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update query", e);
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
            Query query = QueryManager.getInstance().getQuery(id);
            if(query != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Query has been successfully fetched.");
                result.data = query;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get query", e);
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
            List<Query> queries = QueryManager.getInstance().getQueries();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Queries have been successfully fetched.");
            result.list = queries;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get queries", e);
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
            Query query = QueryManager.getInstance().getQuery(id);
            
            QueryManager.getInstance().deleteQuery(id);
            
            Event event = new Event();
            event.type = EventType.DeleteQuery;
            event.data.put("data", query);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Query has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete query", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/execute")
    @Produces("application/json")
    public Response execute(
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
            QueryProcessor processor = QueryManager.getInstance().getQueryProcessor(id);
            if(processor != null) {
                List<QueryItem> items = processor.query();
                
                APIResult result = APIResultUtils.buildOKAPIResult("Query processor has been successfully executed.");
                result.list = items;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Query processor does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get query", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

package org.wilson.world.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.api.util.APIResultUtils;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.joke.JokeInfo;
import org.wilson.world.joke.JokeItem;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.HumorPatternManager;
import org.wilson.world.manager.JokeManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.HumorPattern;

@Path("/joke")
public class JokeAPI {
    private static final Logger logger = Logger.getLogger(JokeAPI.class);
    
    @GET
    @Path("/random")
    @Produces("application/json")
    public Response random(
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
            JokeInfo info = JokeManager.getInstance().randomJokeInfo();
            if(info != null) {
                JokeManager.getInstance().loadJokeInfo(info);
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke is not loaded yet."));
                }
                
                JokeManager.getInstance().removeJokeInfo(info);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Random joke has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random joke does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random joke!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/save")
    @Produces("application/json")
    public Response save(
            @FormParam("id") int id,
            @FormParam("name") String name,
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
            JokeInfo info = JokeManager.getInstance().getJokeInfo(id);
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke is not loaded."));
                }
                
                String ret = JokeManager.getInstance().save(info, name);
                if(ret == null) {
                    APIResult result = APIResultUtils.buildOKAPIResult("Joke has been successfully saved.");
                    return APIResultUtils.buildJSONResponse(result);
                }
                else {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
                }
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to save joke!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_file")
    public Response getFile(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo)
    {
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    String url = ConfigManager.getInstance().getDataDir() + JokeManager.getInstance().getJokeFileName();
                    java.nio.file.Path path = Paths.get(url);
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    logger.error("failed to get file", e);
                    throw new WebApplicationException();
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.TEXT_PLAIN)
                .header("content-disposition","attachment; filename = " + JokeManager.getInstance().getJokeFileName())
                .build();
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
            List<JokeItem> items = JokeManager.getInstance().getJokeItems();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Items have been successfully fetched.");
            result.list = items;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
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
            JokeItem item = JokeManager.getInstance().getJokeItem(id);
            if(item == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke item is not found."));
            }
            
            JokeInfo info = JokeManager.getInstance().load(item);
            
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke is not loaded yet."));
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Joke has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Joke does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get joke!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/set_source")
    @Produces("application/json")
    public Response setSource(
            @QueryParam("source") String source,
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
            JokeManager.getInstance().setCurrent(source);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Source has been successfully set.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to set source", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/train")
    @Produces("application/json")
    public Response train(
            @FormParam("id") int id,
            @FormParam("name") String name,
            @FormParam("content") String content,
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
            if(!StringUtils.isBlank(name) && !StringUtils.isBlank(content)) {
                name = name.trim();
                content = content.trim();
                
                HumorPattern pattern = new HumorPattern();
                pattern.name = name;
                pattern.content = content;
                HumorPatternManager.getInstance().createHumorPattern(pattern);
                
                Event event = new Event();
                event.type = EventType.CreateHumorPattern;
                event.data.put("data", pattern);
                EventManager.getInstance().fireEvent(event);
            }
            
            Event event = new Event();
            event.type = EventType.TrainJoke;
            EventManager.getInstance().fireEvent(event);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Joke has been successfully trained.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to train joke", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

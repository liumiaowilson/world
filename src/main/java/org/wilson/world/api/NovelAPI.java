package org.wilson.world.api;

import java.io.IOException;
import java.net.URISyntaxException;
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
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NovelManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.novel.NovelInfo;
import org.wilson.world.novel.NovelItem;

@Path("/novel")
public class NovelAPI {
    private static final Logger logger = Logger.getLogger(NovelAPI.class);
    
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
            NovelInfo info = NovelManager.getInstance().randomNovelInfo();
            if(info != null) {
                NovelManager.getInstance().loadNovelInfo(info);
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel is not loaded yet."));
                }
                
                NovelManager.getInstance().removeNoveInfo(info);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Random novel has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random novel does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random novel!", e);
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
            NovelInfo info = NovelManager.getInstance().getNovelInfo(id);
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel is not loaded."));
                }
                
                String ret = NovelManager.getInstance().save(info, name);
                if(ret == null) {
                    APIResult result = APIResultUtils.buildOKAPIResult("Novel has been successfully saved.");
                    return APIResultUtils.buildJSONResponse(result);
                }
                else {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
                }
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to save novel!", e);
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
                    String url = ConfigManager.getInstance().getDataDir() + NovelManager.getInstance().getNovelFileName();
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
                .header("content-disposition","attachment; filename = " + NovelManager.getInstance().getNovelFileName())
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
            List<NovelItem> items = NovelManager.getInstance().getNovelItems();
            
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
            NovelItem item = NovelManager.getInstance().getNovelItem(id);
            if(item == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel item is not found."));
            }
            
            boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
            if(!pass) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No valid gallery ticket cound be found."));
            }
            
            NovelInfo info = NovelManager.getInstance().load(item);
            
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel is not loaded yet."));
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Novel has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Novel does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/view_public")
    @Produces("application/json")
    public Response viewPublic(
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            NovelItem item = NovelManager.getInstance().randomNovelItem();
            if(item == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No novel item is found");
            }
            if(!ConfigManager.getInstance().isInDebugMode()) {
                boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                if(!pass) {
                    return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No enough gallery ticket to view the novel");
                }
            }
            
            NovelInfo info = NovelManager.getInstance().load(item);
            
            request.getSession().setAttribute("world-public-novel", info.html);
            
            return APIResultUtils.buildURLResponse(request, "view_novel.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view novel", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
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
            NovelManager.getInstance().setCurrent(source);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Source has been successfully set.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to set source", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

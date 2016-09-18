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
import org.wilson.world.etiquette.EtiquetteInfo;
import org.wilson.world.etiquette.EtiquetteItem;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.EtiquetteManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("/etiquette")
public class EtiquetteAPI {
    private static final Logger logger = Logger.getLogger(EtiquetteAPI.class);
    
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
            EtiquetteInfo info = EtiquetteManager.getInstance().randomEtiquetteInfo();
            if(info != null) {
                EtiquetteManager.getInstance().loadEtiquetteInfo(info);
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette is not loaded yet."));
                }
                
                EtiquetteManager.getInstance().removeEtiquetteInfo(info);
                
                APIResult result = APIResultUtils.buildOKAPIResult("Random etiquette has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random etiquette does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random etiquette!", e);
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
            EtiquetteInfo info = EtiquetteManager.getInstance().getEtiquetteInfo(id);
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette is not loaded."));
                }
                
                String ret = EtiquetteManager.getInstance().save(info, name);
                if(ret == null) {
                    APIResult result = APIResultUtils.buildOKAPIResult("Etiquette has been successfully saved.");
                    return APIResultUtils.buildJSONResponse(result);
                }
                else {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
                }
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to save etiquette!", e);
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
                    String url = ConfigManager.getInstance().getDataDir() + EtiquetteManager.getInstance().getEtiquetteFileName();
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
                .header("content-disposition","attachment; filename = " + EtiquetteManager.getInstance().getEtiquetteFileName())
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
            List<EtiquetteItem> items = EtiquetteManager.getInstance().getEtiquetteItems();
            
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
            EtiquetteItem item = EtiquetteManager.getInstance().getEtiquetteItem(id);
            if(item == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette item is not found."));
            }
            
            EtiquetteInfo info = EtiquetteManager.getInstance().load(item);
            
            if(info != null) {
                if(info.html == null) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette is not loaded yet."));
                }
                
                APIResult result = APIResultUtils.buildOKAPIResult("Etiquette has been successfully fetched.");
                result.data = info;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Etiquette does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get etiquette!", e);
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
            EtiquetteManager.getInstance().setCurrent(source);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Source has been successfully set.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to set source", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/clean")
    @Produces("application/json")
    public Response clean(
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
            EtiquetteManager.getInstance().getEtiquettesRemoved().clear();
            APIResult result = APIResultUtils.buildOKAPIResult("Removed etiquettes have been successfully cleaned.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to clean removed etiquettes!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

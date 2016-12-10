package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.MangaManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manga.Manga;
import org.wilson.world.manga.MangaCreator;
import org.wilson.world.model.APIResult;

@Path("manga")
public class MangaAPI {
    private static final Logger logger = Logger.getLogger(MangaAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("creator") String creator, 
            @FormParam("parameters") String parameters,
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
        
        if(StringUtils.isBlank(creator)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Creator should be provided."));
        }
        creator = creator.trim();
        if(StringUtils.isBlank(parameters)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Parameters should be provided."));
        }
        parameters = parameters.trim();
        
        try {
            String ret = MangaManager.getInstance().createManga(creator, parameters);
            if(ret == null) {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Manga has been successfully created."));
            }
            else {
            	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to create manga", e);
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
            List<Manga> mangas = MangaManager.getInstance().getMangas();
            Collections.sort(mangas, new Comparator<Manga>(){

				@Override
				public int compare(Manga o1, Manga o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Mangas have been successfully fetched.");
            result.list = mangas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get mangas", e);
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
            MangaManager.getInstance().deleteManaga(id);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Manga has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete manga", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/get_parameters_hint")
    @Produces("application/json")
    public Response getParametersHint(
            @QueryParam("creator") String creator,
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
        
        if(StringUtils.isBlank(creator)) {
        	return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Creator is needed."));
        }
        
        try {
        	MangaCreator mangaCreator = MangaManager.getInstance().getMangaCreator(creator);
        	if(mangaCreator != null) {
        		String hint = mangaCreator.getParametersHint();
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(hint));
        	}
        	else {
        		return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such creator could be found."));
        	}
        }
        catch(Exception e) {
            logger.error("failed to get parameters hint", e);
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
            Manga manga = MangaManager.getInstance().randomManga();
            if(manga == null) {
                return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No manga is found");
            }
            
            if(!ConfigManager.getInstance().isInDebugMode()) {
                boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
                if(!pass) {
                    return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No enough gallery ticket to view the manga");
                }
            }
            
            request.getSession().setAttribute("world-public-manga", manga);
            
            return APIResultUtils.buildURLResponse(request, "public/view_manga.jsp");
        }
        catch(Exception e) {
            logger.error("failed to view manga", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
}

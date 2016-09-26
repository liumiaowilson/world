package org.wilson.world.api;

import java.util.Collections;
import java.util.Comparator;
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
import org.wilson.world.dress.DressColor;
import org.wilson.world.dress.DressColorFamily;
import org.wilson.world.dress.HomeBase;
import org.wilson.world.dress.SeasonEnergy;
import org.wilson.world.manager.DressManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;

@Path("dress")
public class DressAPI {
    private static final Logger logger = Logger.getLogger(DressAPI.class);
    
    @GET
    @Path("/list_energy")
    @Produces("application/json")
    public Response listSeasonEnergy(
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
            List<SeasonEnergy> energies = DressManager.getInstance().getSeasonEnergies();
            
            Collections.sort(energies, new Comparator<SeasonEnergy>() {

                @Override
                public int compare(SeasonEnergy o1, SeasonEnergy o2) {
                    return o1.id - o2.id;
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Season energies have been successfully fetched.");
            result.list = energies;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get season energies", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_family")
    @Produces("application/json")
    public Response listDressColorFamily(
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
            List<DressColorFamily> families = DressManager.getInstance().getDressColorFamilies();
            
            Collections.sort(families, new Comparator<DressColorFamily>() {

                @Override
                public int compare(DressColorFamily o1, DressColorFamily o2) {
                    return o1.id - o2.id;
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Dress color families have been successfully fetched.");
            result.list = families;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get dress color families", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_color")
    @Produces("application/json")
    public Response listDressColor(
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
            List<DressColor> colors = DressManager.getInstance().getDressColors();
            
            Collections.sort(colors, new Comparator<DressColor>() {

                @Override
                public int compare(DressColor o1, DressColor o2) {
                    return o1.id - o2.id;
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Dress colors have been successfully fetched.");
            result.list = colors;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get dress colors", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/list_homebase")
    @Produces("application/json")
    public Response listHomeBase(
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
            List<HomeBase> homebases = DressManager.getInstance().getHomeBases();
            
            Collections.sort(homebases, new Comparator<HomeBase>() {

                @Override
                public int compare(HomeBase o1, HomeBase o2) {
                    return o1.id - o2.id;
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("HomeBases have been successfully fetched.");
            result.list = homebases;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get homebases", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

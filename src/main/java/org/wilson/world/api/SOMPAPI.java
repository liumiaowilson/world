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
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.SOMPManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.SOMP;
import org.wilson.world.somp.SOMPQuiz;

@Path("somp")
public class SOMPAPI {
    private static final Logger logger = Logger.getLogger(SOMPAPI.class);
    
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
            SOMP pattern = SOMPManager.getInstance().getSOMP(id);
            if(pattern != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("SOMP has been successfully fetched.");
                result.data = pattern;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("SOMP does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get pattern", e);
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
            List<SOMP> patterns = SOMPManager.getInstance().getSOMPs();
            
            Collections.sort(patterns, new Comparator<SOMP>() {

                @Override
                public int compare(SOMP o1, SOMP o2) {
                    return o1.name.compareTo(o2.name);
                }
                
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("SOMPs have been successfully fetched.");
            result.list = patterns;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get patterns", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/do_quiz")
    @Produces("application/json")
    public Response doQuiz(
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
            SOMPQuiz quiz = (SOMPQuiz) QuizDataManager.getInstance().getQuizOfClass(SOMPQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doSOMPQuiz()");
            
            APIResult result = APIResultUtils.buildOKAPIResult("Quiz has been successfully fetched.");
            result.data = quiz.getId();
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to do quiz", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

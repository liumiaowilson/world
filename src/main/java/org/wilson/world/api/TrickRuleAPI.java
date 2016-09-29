package org.wilson.world.api;

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
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.TrickRuleManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.TrickRule;
import org.wilson.world.trickrule.TrickRuleQuiz;

@Path("trick_rule")
public class TrickRuleAPI {
    private static final Logger logger = Logger.getLogger(TrickRuleAPI.class);
    
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
            TrickRule rule = TrickRuleManager.getInstance().getTrickRule(id);
            if(rule != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("TrickRule has been successfully fetched.");
                result.data = rule;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("TrickRule does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get trick rule", e);
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
            List<TrickRule> rules = TrickRuleManager.getInstance().getTrickRules();
            
            APIResult result = APIResultUtils.buildOKAPIResult("TrickRules have been successfully fetched.");
            result.list = rules;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get trick rules", e);
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
            TrickRuleQuiz quiz = (TrickRuleQuiz) QuizDataManager.getInstance().getQuizOfClass(TrickRuleQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doTrickRuleQuiz()");
            
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

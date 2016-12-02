package org.wilson.world.api;

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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.interview.InterviewQuiz;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.InterviewManager;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Interview;

@Path("interview")
public class InterviewAPI {
    private static final Logger logger = Logger.getLogger(InterviewAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("question") String question,
            @FormParam("answer") String answer,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(question)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview question should be provided."));
        }
        question = question.trim();
        if(StringUtils.isBlank(answer)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview answer should be provided."));
        }
        answer = answer.trim();
        
        try {
            Interview interview = new Interview();
            interview.name = name;
            interview.question = question;
            interview.answer = answer;
            InterviewManager.getInstance().createInterview(interview);
            
            Event event = new Event();
            event.type = EventType.CreateInterview;
            event.data.put("data", interview);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Interview has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create interview", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("question") String question,
            @FormParam("answer") String answer,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(question)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview question should be provided."));
        }
        question = question.trim();
        if(StringUtils.isBlank(answer)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview answer should be provided."));
        }
        answer = answer.trim();
        
        try {
            Interview oldInterview = InterviewManager.getInstance().getInterview(id);
            
            Interview interview = new Interview();
            interview.id = id;
            interview.name = name;
            interview.question = question;
            interview.answer = answer;
            InterviewManager.getInstance().updateInterview(interview);
            
            Event event = new Event();
            event.type = EventType.UpdateInterview;
            event.data.put("old_data", oldInterview);
            event.data.put("new_data", interview);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Interview has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update interview", e);
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
            Interview interview = InterviewManager.getInstance().getInterview(id);
            if(interview != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Interview has been successfully fetched.");
                result.data = interview;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Interview does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get interview", e);
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
            List<Interview> interviews = InterviewManager.getInstance().getInterviews();
            Collections.sort(interviews, new Comparator<Interview>(){

				@Override
				public int compare(Interview o1, Interview o2) {
					return Integer.compare(o1.id, o2.id);
				}
            	
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Interviews have been successfully fetched.");
            result.list = interviews;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get interviews", e);
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
            Interview interview = InterviewManager.getInstance().getInterview(id);
            
            InterviewManager.getInstance().deleteInterview(id);
            
            Event event = new Event();
            event.type = EventType.DeleteInterview;
            event.data.put("data", interview);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Interview has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete interview", e);
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
            InterviewQuiz quiz = (InterviewQuiz) QuizDataManager.getInstance().getQuizOfClass(InterviewQuiz.class);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz could be found."));
            }
            QuizDataManager.getInstance().clearQuizPaper();
            QuizDataManager.getInstance().setRedoUrl("javascript:doInterviewQuiz()");
            
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

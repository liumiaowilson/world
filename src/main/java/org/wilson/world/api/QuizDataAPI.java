package org.wilson.world.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.QuizDataManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.QuizData;
import org.wilson.world.quiz.Quiz;
import org.wilson.world.quiz.QuizPaper;
import org.wilson.world.quiz.QuizResult;

@Path("quiz_data")
public class QuizDataAPI {
    private static final Logger logger = Logger.getLogger(QuizDataAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("processor") String processor,
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(processor)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data processor should be provided."));
        }
        processor = processor.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data content should be provided."));
        }
        content = content.trim();
        
        try {
            QuizData data = new QuizData();
            data.name = name;
            data.description = description;
            data.processor = processor;
            data.content = content;
            QuizDataManager.getInstance().createQuizData(data);
            
            Event event = new Event();
            event.type = EventType.CreateQuizData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("description") String description,
            @FormParam("processor") String processor,
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data description should be provided."));
        }
        description = description.trim();
        if(StringUtils.isBlank(processor)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data processor should be provided."));
        }
        processor = processor.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data content should be provided."));
        }
        content = content.trim();
        
        try {
            QuizData oldData = QuizDataManager.getInstance().getQuizData(id);
            
            QuizData data = new QuizData();
            data.id = id;
            data.name = name;
            data.description = description;
            data.processor = processor;
            data.content = content;
            QuizDataManager.getInstance().updateQuizData(data);
            
            Event event = new Event();
            event.type = EventType.UpdateQuizData;
            event.data.put("old_data", oldData);
            event.data.put("new_data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
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
            QuizData data = QuizDataManager.getInstance().getQuizData(id);
            if(data != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Data has been successfully fetched.");
                result.data = data;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Data does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
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
            List<QuizData> datas = QuizDataManager.getInstance().getQuizDatas();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Datas have been successfully fetched.");
            result.list = datas;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
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
            QuizData data = QuizDataManager.getInstance().getQuizData(id);
            
            QuizDataManager.getInstance().deleteQuizData(id);
            
            Event event = new Event();
            event.type = EventType.DeleteQuizData;
            event.data.put("data", data);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Data has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/validate_content")
    @Produces("application/json")
    public Response validateContent(
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
        
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Content should be provided."));
        }
        content = content.trim();
        
        try {
            String ret = QuizDataManager.getInstance().validateContent(content);
            if(ret == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Content has been successfully validated."));
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(ret));
            }
        }
        catch(Exception e) {
            logger.error("failed to validate content", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/do_quiz")
    @Produces("application/json")
    public Response doQuiz(
            @FormParam("id") int id, 
            @FormParam("itemId") int itemId,
            @FormParam("selection") String selection,
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
        
        if(StringUtils.isBlank(selection)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Selection should be provided."));
        }
        selection = selection.trim();
        
        try {
            Quiz quiz = QuizDataManager.getInstance().getQuiz(id);
            if(quiz == null) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("No such quiz can be found."));
            }
            QuizPaper paper = QuizDataManager.getInstance().getQuizPaper(quiz);
            String [] items = selection.split(",");
            int [] ids = new int [items.length];
            for(int i = 0; i < items.length; i++) {
                ids[i] = Integer.parseInt(items[i]);
            }
            paper.select(itemId, ids);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Quiz paper has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to do quiz", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    

    @POST
    @Path("/done")
    @Produces("application/json")
    public Response done(
            @FormParam("key") String key,
            @FormParam("id") int id,
            @FormParam("selection") List<String> selections,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        Quiz quiz = QuizDataManager.getInstance().getQuiz(id);
        if(quiz == null) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "No such quiz can be found");
        }
        QuizPaper paper = QuizDataManager.getInstance().getQuizPaper(quiz);
        Map<Integer, List<Integer>> answer = new HashMap<Integer, List<Integer>>();
        for(String selection : selections) {
            try {
                String [] items = selection.split("_");
                int item_id = Integer.parseInt(items[0]);
                int option_id = Integer.parseInt(items[1]);
                List<Integer> options = answer.get(item_id);
                if(options == null) {
                    options = new ArrayList<Integer>();
                    answer.put(item_id, options);
                }
                options.add(option_id);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
        
        for(Entry<Integer, List<Integer>> entry : answer.entrySet()) {
            int itemId = entry.getKey();
            List<Integer> options = entry.getValue();
            int [] selectedOptions = new int [options.size()];
            for(int i = 0; i < selectedOptions.length; i++) {
                selectedOptions[i] = options.get(i);
            }
            paper.select(itemId, selectedOptions);
        }
        
        QuizResult result = quiz.process(paper);
        request.getSession().setAttribute("world-public-quiz-result", result.message);
        
        return APIResultUtils.buildURLResponse(request, "quiz.jsp?id=" + id);
    }
}

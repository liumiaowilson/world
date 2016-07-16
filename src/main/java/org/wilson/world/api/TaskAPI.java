package org.wilson.world.api;

import java.util.ArrayList;
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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StarManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Task;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("task")
public class TaskAPI {
    private static final Logger logger = Logger.getLogger(TaskAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task content should be provided."));
        }
        content = content.trim();
        
        try {
            Task task = new Task();
            task.name = name;
            task.content = content;
            long createdTime = System.currentTimeMillis();
            task.createdTime = createdTime;
            task.modifiedTime = createdTime;
            TaskManager.getInstance().createTask(task);
            
            Event event = new Event();
            event.type = EventType.CreateTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task content should be provided."));
        }
        content = content.trim();
        
        try {
            Task oldTask = TaskManager.getInstance().getTask(id);
            
            Task task = new Task();
            task.id = id;
            task.name = name;
            task.content = content;
            task.modifiedTime = System.currentTimeMillis();
            TaskManager.getInstance().updateTask(task);
            
            Event event = new Event();
            event.type = EventType.UpdateTask;
            event.data.put("old_data", oldTask);
            event.data.put("new_data", task);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(task);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update task", e);
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
            Task task = TaskManager.getInstance().getTask(id);
            if(task != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Task has been successfully fetched.");
                result.data = task;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get task", e);
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
            List<Task> tasks = TaskManager.getInstance().getTasks();
            List<String> markedIds = MarkManager.getInstance().getMarked(TaskManager.NAME);
            if(markedIds != null) {
                for(Task task : tasks) {
                    String id = String.valueOf(task.id);
                    if(markedIds.contains(id)) {
                        task.marked = true;
                    }
                    else {
                        task.marked = false;
                    }
                }
            }
            
            StarManager.getInstance().process(tasks);
            
            Collections.sort(tasks, new Comparator<Task>(){
                @Override
                public int compare(Task t1, Task t2) {
                    if(t1.marked == t2.marked) {
                        if(t1.starred == t2.starred) {
                            return -(t1.id - t2.id);
                        }
                        else if(t1.starred && !t2.starred) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                    else if(t1.marked && !t2.marked) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Tasks have been successfully fetched.");
            result.list = tasks;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get tasks", e);
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
            Task task = TaskManager.getInstance().getTask(id);
            
            TaskManager.getInstance().deleteTask(id);
            
            Event event = new Event();
            event.type = EventType.DeleteTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(task);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/split")
    @Produces("application/json")
    public Response split(
            @FormParam("id") int id,
            @FormParam("tasks") String tasks,
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
        if(StringUtils.isBlank(tasks)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New tasks are needed."));
        }
        
        try {
            List<Task> newTasks = new ArrayList<Task>();
            JSONArray json = JSONArray.fromObject(tasks);
            for(int i = 0; i < json.size(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String name = obj.getString("name").trim();
                String content = obj.getString("content").trim();
                if(StringUtils.isBlank(name)) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New task name is needed."));
                }
                if(StringUtils.isBlank(content)) {
                    content = name;
                }
                Task newTask = new Task();
                newTask.name = name;
                newTask.content = content;
                long createdTime = System.currentTimeMillis();
                newTask.createdTime = createdTime;
                newTask.modifiedTime = createdTime;
                newTasks.add(newTask);
            }
            
            Task oldTask = TaskManager.getInstance().getTask(id);
            TaskManager.getInstance().deleteTask(id);
            
            for(Task newTask : newTasks) {
                TaskManager.getInstance().createTask(newTask);
            }
            
            Event event = new Event();
            event.type = EventType.SplitTask;
            event.data.put("old_data", oldTask);
            event.data.put("new_data", newTasks);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(oldTask);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully splitted."));
        }
        catch(Exception e) {
            logger.error("failed to split task!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to split task."));
        }
    }
    
    @POST
    @Path("/merge")
    @Produces("application/json")
    public Response merge(
            @FormParam("id") int id,
            @FormParam("ids") String ids,
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
        if(StringUtils.isBlank(ids)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Tasks to be merged are needed."));
        }
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New task name is needed."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("New task content is needed."));
        }
        content = content.trim();
        
        String [] items = ids.trim().split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for(String item : items) {
            try {
                int mergeId = Integer.parseInt(item);
                if(!idList.contains(mergeId)) {
                    idList.add(mergeId);
                }
            }
            catch(Exception e) {
                logger.error("failed to parse id", e);
            }
        }
        if(!idList.contains(id)) {
            idList.add(id);
        }
        
        try {
            Task newTask = new Task();
            newTask.name = name;
            newTask.content = content;
            long createdTime = System.currentTimeMillis();
            newTask.createdTime = createdTime;
            newTask.modifiedTime = createdTime;
            TaskManager.getInstance().createTask(newTask);
            
            List<Task> oldTasks = new ArrayList<Task>();
            for(int mergeId : idList) {
                Task oldTask = TaskManager.getInstance().getTask(mergeId);
                oldTasks.add(oldTask);
                TaskManager.getInstance().deleteTask(mergeId);
                MarkManager.getInstance().unmark(TaskManager.NAME, String.valueOf(mergeId));
            }
            
            Event event = new Event();
            event.type = EventType.MergeTask;
            event.data.put("old_data", oldTasks);
            event.data.put("new_data", newTask);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(oldTasks);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Tasks have been successfully merged."));
        }
        catch(Exception e) {
            logger.error("failed to merge tasks!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to merge tasks."));
        }
    }
}

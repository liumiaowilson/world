package org.wilson.world.api;

import java.net.URISyntaxException;
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
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.MarkManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.StarManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskAttrManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.manager.URLManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.model.TaskTag;
import org.wilson.world.task.TaskIterator;

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
            @FormParam("attrs") String attrs,
            @FormParam("tags") String tags,
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
            
            if(!StringUtils.isBlank(attrs)) {
                JSONArray attrArray = JSONArray.fromObject(attrs);
                List<TaskAttr> attrList = new ArrayList<TaskAttr>();
                for(int i = 0; i < attrArray.size(); i++) {
                    JSONObject attrObj = attrArray.getJSONObject(i);
                    String p_name = attrObj.getString("name");
                    String p_value = attrObj.getString("value");
                    TaskAttr attr = new TaskAttr();
                    attr.name = p_name.trim();
                    attr.value = p_value.trim();
                    attrList.add(attr);
                }
                task.attrs = attrList;
            }
            
            if(!StringUtils.isBlank(tags)) {
                TaskTag tag = new TaskTag();
                tag.tags = tags;
                task.tag = tag;
            }
            
            TaskManager.getInstance().createTask(task);
            
            Event event = new Event();
            event.type = EventType.CreateTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().reset();
            
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
            @FormParam("attrs") String attrs,
            @FormParam("tags") String tags,
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
            task.createdTime = oldTask.createdTime;
            task.modifiedTime = System.currentTimeMillis();
            
            if(!StringUtils.isBlank(attrs)) {
                JSONArray attrArray = JSONArray.fromObject(attrs);
                List<TaskAttr> attrList = new ArrayList<TaskAttr>();
                for(int i = 0; i < attrArray.size(); i++) {
                    JSONObject attrObj = attrArray.getJSONObject(i);
                    int p_id = attrObj.getInt("id");
                    String p_name = attrObj.getString("name");
                    String p_value = attrObj.getString("value");
                    TaskAttr attr = new TaskAttr();
                    attr.id = p_id;
                    attr.name = p_name.trim();
                    attr.value = p_value.trim();
                    attrList.add(attr);
                }
                task.attrs = attrList;
            }
            
            if(!StringUtils.isBlank(tags)) {
                TaskTag tag = new TaskTag();
                tag.taskId = task.id;
                tag.tags = tags;
                task.tag = tag;
            }
            
            
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
                newTask.tag = TaskTag.clone(oldTask.tag);
            }
            
            TaskAttr beforeAttr = TaskManager.getInstance().getTaskAttr(oldTask, TaskAttrDefManager.DEF_BEFORE);
            String beforeId = null;
            if(beforeAttr != null) {
                if(!StringUtils.isBlank(beforeAttr.value)) {
                    beforeId = beforeAttr.value;
                }
            }
            
            TaskAttr afterAttr = TaskManager.getInstance().getTaskAttr(oldTask, TaskAttrDefManager.DEF_AFTER);
            String afterId = null;
            if(afterAttr != null) {
                if(!StringUtils.isBlank(afterAttr.value)) {
                    afterId = afterAttr.value;
                }
            }
            
            for(Task newTask : newTasks) {
                for(TaskAttr attr : oldTask.attrs) {
                    if(!TaskAttrDefManager.DEF_BEFORE.equals(attr.name) && !TaskAttrDefManager.DEF_AFTER.equals(attr.name)) {
                        newTask.attrs.add(TaskAttrManager.getInstance().copyTaskAttr(attr));
                    }
                }
            }
            
            for(int i = 0; i < newTasks.size(); i++) {
                Task newTask = newTasks.get(i);
                if(i == 0) {
                    if(afterId != null) {
                        newTask.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_AFTER, afterId));
                    }
                }
                
                if(i >= 1) {
                    newTask.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_AFTER, String.valueOf(newTasks.get(i - 1).id)));
                }
                
                TaskManager.getInstance().createTask(newTask);
                
                if(i == newTasks.size() - 1) {
                    if(beforeId != null) {
                        newTask.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_BEFORE, beforeId));
                    }
                }
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
            List<Task> oldTasks = new ArrayList<Task>();
            for(int mergeId : idList) {
                Task oldTask = TaskManager.getInstance().getTask(mergeId);
                oldTasks.add(oldTask);
                TaskManager.getInstance().deleteTask(mergeId);
                MarkManager.getInstance().unmark(TaskManager.NAME, String.valueOf(mergeId));
            }
            
            Task newTask = new Task();
            newTask.name = name;
            newTask.content = content;
            long createdTime = System.currentTimeMillis();
            newTask.createdTime = createdTime;
            newTask.modifiedTime = createdTime;
            
            Task lastOldTask = oldTasks.get(oldTasks.size() - 1);
            for(TaskAttr attr : lastOldTask.attrs) {
                newTask.attrs.add(TaskAttrManager.getInstance().copyTaskAttr(attr));
            }
            
            newTask.tag = TaskTag.clone(lastOldTask.tag);
            
            TaskManager.getInstance().createTask(newTask);
            
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
    
    @GET
    @Path("/finish")
    @Produces("application/json")
    public Response finish(
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
            event.type = EventType.FinishTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(task);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully finished."));
        }
        catch(Exception e) {
            logger.error("failed to finish task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/abandon")
    @Produces("application/json")
    public Response abandon(
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
            event.type = EventType.AbandonTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            StarManager.getInstance().postProcess(task);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Task has been successfully abandoned."));
        }
        catch(Exception e) {
            logger.error("failed to abandon task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
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
            Task task = TaskManager.getInstance().randomTask();
            if(task != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Random task has been successfully fetched.");
                result.data = task;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Random task does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get random task!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to batch create ideas."));
        }
    }
    
    @GET
    @Path("/prev")
    @Produces("application/json")
    public Response prev(
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
            List<Task> tasks = TaskManager.getInstance().getTasks();
            int prev = -1;
            for(int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if(task.id == id) {
                    prev = i - 1;
                    break;
                }
            }
            
            if(prev >= 0) {
                prev = tasks.get(prev).id;
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Previous task has been successfully fetched.");
            result.data = prev;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get previous task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/next")
    @Produces("application/json")
    public Response next(
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
            List<Task> tasks = TaskManager.getInstance().getTasks();
            int next = -1;
            for(int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if(task.id == id) {
                    next = i + 1;
                    break;
                }
            }
            
            if(next >= tasks.size()) {
                next = -1;
            }
            
            if(next >= 0) {
                next = tasks.get(next).id;
            }
            
            APIResult result = APIResultUtils.buildOKAPIResult("Next task has been successfully fetched.");
            result.data = next;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get next task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/gen_dependent")
    @Produces("application/json")
    public Response genDependent(
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
            Task task = TaskManager.getInstance().getTask(id);
            if(task != null) {
                Task newTask = new Task();
                newTask.name = name;
                newTask.content = name;
                newTask.createdTime = System.currentTimeMillis();
                newTask.modifiedTime = newTask.createdTime;
                for(TaskAttr attr : task.attrs) {
                    TaskAttr newAttr = new TaskAttr();
                    newAttr.name = attr.name;
                    newAttr.value = attr.value;
                    newTask.attrs.add(newAttr);
                }
                if(task.tag != null) {
                    TaskTag tag = new TaskTag();
                    tag.tags = task.tag.tags;
                    newTask.tag = tag;
                }
                
                TaskAttr attr = TaskAttr.getTaskAttr(newTask.attrs, TaskAttrDefManager.DEF_BEFORE);
                if(attr != null) {
                    attr.value = String.valueOf(task.id);
                }
                else {
                    attr = TaskAttr.create(TaskAttrDefManager.DEF_BEFORE, String.valueOf(task.id));
                    newTask.attrs.add(attr);
                }
                
                TaskManager.getInstance().createTask(newTask);
                
                Event event = new Event();
                event.type = EventType.CreateTask;
                event.data.put("data", newTask);
                EventManager.getInstance().fireEvent(event);
                
                StarManager.getInstance().reset();
                
                APIResult result = APIResultUtils.buildOKAPIResult("Dependent task has been successfully generated.");
                result.data = newTask;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to generate dependent task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/gen_child")
    @Produces("application/json")
    public Response genChild(
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
            Task task = TaskManager.getInstance().getTask(id);
            if(task != null) {
                if(TaskManager.getInstance().isChildTask(task)) {
                    return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cannot create nested projects."));
                }
                
                Task newTask = new Task();
                newTask.name = name;
                newTask.content = name;
                newTask.createdTime = System.currentTimeMillis();
                newTask.modifiedTime = newTask.createdTime;
                for(TaskAttr attr : task.attrs) {
                    TaskAttr newAttr = new TaskAttr();
                    newAttr.name = attr.name;
                    newAttr.value = attr.value;
                    newTask.attrs.add(newAttr);
                }
                if(task.tag != null) {
                    TaskTag tag = new TaskTag();
                    tag.tags = task.tag.tags;
                    newTask.tag = tag;
                }
                
                //must be new attribute
                TaskAttr attr = TaskAttr.create(TaskAttrDefManager.DEF_PARENT, String.valueOf(task.id));
                newTask.attrs.add(attr);
                
                TaskManager.getInstance().createTask(newTask);
                
                Event event = new Event();
                event.type = EventType.CreateTask;
                event.data.put("data", newTask);
                EventManager.getInstance().fireEvent(event);
                
                StarManager.getInstance().reset();
                
                APIResult result = APIResultUtils.buildOKAPIResult("Child task has been successfully generated.");
                result.data = newTask;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Task does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to generate child task", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/start_iterator")
    @Produces("application/json")
    public Response startIterator(
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
            String mode = NotifyManager.getInstance().getModeStatus();
            if(!StringUtils.isBlank(mode)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Cannot start task iterator because of other existing mode."));
            }
            
            TaskIterator.getInstance().setEnabled(true);
            String basePath = URLManager.getInstance().getBaseUrl();
            URLManager.getInstance().setCenterUrl(basePath + "/jsp/task_edit.jsp?dir=next");
            APIResult result = APIResultUtils.buildOKAPIResult("Task iterator has been successfully started.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to start iterator", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/stop_iterator")
    @Produces("application/json")
    public Response stopIterator(
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
            TaskIterator.getInstance().setEnabled(false);
            URLManager.getInstance().setCenterUrl(null);
            APIResult result = APIResultUtils.buildOKAPIResult("Task iterator has been successfully stopped.");
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to stop iterator", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/create_public")
    @Produces("application/json")
    public Response createPublic(
            @FormParam("key") String key,
            @FormParam("content") String content,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", "Content should be provided.");
        }
        content = content.trim();
        
        try {
            String name = content;
            if(name.length() > 20) {
                name = name.substring(0, 20);
            }
            
            Task task = Task.newTask(name, content);
            TaskManager.getInstance().createTask(task);
            
            Event event = new Event();
            event.type = EventType.CreateTask;
            event.data.put("data", task);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildURLResponse(request, "public/post_task.jsp");
        }
        catch(Exception e) {
            logger.error("failed to create item", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @POST
    @Path("/list_public")
    @Produces("application/json")
    public Response listPublic(
            @FormParam("key") String key,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        try {
            List<Task> tasks = TaskManager.getInstance().getTasks();
            StringBuffer sb = new StringBuffer();
            for(Task task : tasks) {
                sb.append(task.name + "<br/>");
            }
            
            request.getSession().setAttribute("world-public-tasks", sb.toString());
            
            return APIResultUtils.buildURLResponse(request, "public/list_task.jsp");
        }
        catch(Exception e) {
            logger.error("failed to list tasks", e);
            return APIResultUtils.buildURLResponse(request, "public_error.jsp", e.getMessage());
        }
    }
    
    @POST
    @Path("/finish_outdoor")
    @Produces("application/json")
    public Response finishOutdoor(
            @FormParam("key") String key,
            @FormParam("id") List<String> ids,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo) throws URISyntaxException {
        String k = DataManager.getInstance().getValue("public.key");
        if(k == null || !k.equals(key)) {
            return APIResultUtils.buildURLResponse(request, "public_error.jsp");
        }
        
        if(ids != null && !ids.isEmpty()) {
            for(String idStr : ids) {
                try {
                    int id = Integer.parseInt(idStr);
                    Task task = TaskManager.getInstance().getTask(id);
                    if(task != null) {
                        String outdoor = task.getRealValue(TaskAttrDefManager.DEF_OUTDOOR);
                        if(!StringUtils.isBlank(outdoor)) {
                            TaskManager.getInstance().deleteTask(id);
                            
                            Event event = new Event();
                            event.type = EventType.FinishTask;
                            event.data.put("data", task);
                            EventManager.getInstance().fireEvent(event);
                            
                            StarManager.getInstance().postProcess(task);
                        }
                    }
                }
                catch(Exception e) {
                    logger.error(e);
                }
            }
        }
        
        return APIResultUtils.buildURLResponse(request, "public/finish_task.jsp");
    }
}

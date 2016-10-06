package org.wilson.world.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import org.wilson.world.console.FileInfo;
import org.wilson.world.console.MemoryInfo;
import org.wilson.world.console.ObjectGraphInfo;
import org.wilson.world.console.RequestInfo;
import org.wilson.world.console.RequestStats;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.manager.DAOManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ScriptManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.QueryResult;
import org.wilson.world.script.FieldInfo;
import org.wilson.world.script.MethodInfo;
import org.wilson.world.script.ObjectInfo;
import org.wilson.world.util.TimeUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("console")
public class ConsoleAPI {
    private static final Logger logger = Logger.getLogger(ConsoleAPI.class);
    
    @POST
    @Path("/set_key")
    @Produces("application/json")
    public Response setKey(
            @FormParam("key") String key,
            @FormParam("toolbarPolicy") String toolbarPolicy,
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
        
        if(StringUtils.isBlank(key)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Key is needed."));
        }
        key = key.trim();
        
        try {
            DataManager.getInstance().setValue("public.key", key);
            ConsoleManager.getInstance().setToolbarPolicyAsString(toolbarPolicy);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Key has been successfully set."));
        }
        catch(Exception e) {
            logger.error("failed to set key", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to set key"));
        }
    }
    
    @POST
    @Path("/run")
    @Produces("application/json")
    public Response run(
            @FormParam("cmd") String cmd,
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
        
        if(StringUtils.isBlank(cmd)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Command is needed."));
        }
        cmd = cmd.trim();
        
        String output = ConsoleManager.getInstance().run(cmd);
        
        return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(output));
    }
    
    @POST
    @Path("/eval")
    @Produces("application/json")
    public Response eval(
            @FormParam("script") String script,
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
        
        if(StringUtils.isBlank(script)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Script is needed."));
        }
        script = script.trim();
        
        try {
            Object ret = ScriptManager.getInstance().run(script);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(String.valueOf(ret)));
        }
        catch(Exception e) {
            logger.error("failed to evaluate script", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/execute")
    @Produces("application/json")
    public Response execute(
            @FormParam("sql") String sql,
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
        
        if(StringUtils.isBlank(sql)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("SQL is needed."));
        }
        sql = sql.trim();
        
        try {
            QueryResult result = ConsoleManager.getInstance().execute(sql);
            APIResult ret = APIResultUtils.buildOKAPIResult("SQL executed successfully.");
            ret.data = result;
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to execute sql!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to execute sql."));
        }
    }
    
    @GET
    @Path("/delete_logs")
    @Produces("application/json")
    public Response deleteLogs(
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
            String result = ConsoleManager.getInstance().deleteLogs();
            APIResult ret = APIResultUtils.buildOKAPIResult(result);
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to delete logs!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to delete logs."));
        }
    }
    
    @GET
    @Path("/release_memory")
    @Produces("application/json")
    public Response releaseMemory(
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
            ConsoleManager.getInstance().releaseMemory();
            APIResult ret = APIResultUtils.buildOKAPIResult("Memory released successfully.");
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to release memory!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to release memory."));
        }
    }
    
    @POST
    @Path("/upload_config")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadConfig(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @QueryParam("token") String token,
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @Context UriInfo uriInfo) throws URISyntaxException {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        String uploadedFileLocation = ConfigManager.getInstance().getConfigOverrideFilePath();
        
        // save it
        writeToFile(uploadedInputStream, uploadedFileLocation);

        Event event = new Event();
        event.type = EventType.ConfigOverrideUploaded;
        EventManager.getInstance().fireEvent(event);
        
        return APIResultUtils.buildURLResponse(request, "jsp/config.jsp");
    }
    
    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("failed to write to file", e);
        }
    }
    
    @GET
    @Path("/download_log")
    public Response downloadLog(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo)
    {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    String url = null;
                    if(ConfigManager.getInstance().isOpenShiftApp()) {
                        url = "../app-root/logs/jbossews.log";
                    }
                    else {
                        url = "../logs/catalina.out";
                    }
                    java.nio.file.Path path = Paths.get(url);
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    logger.error("failed to download file", e);
                    throw new WebApplicationException();
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = world.log")
                .build();
    }
    
    @GET
    @Path("/export")
    public Response exportData(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo)
    {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    byte [] data = DAOManager.getInstance().exportData().getBytes();
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    logger.error("failed to export data", e);
                    throw new WebApplicationException();
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = world.sql")
                .build();
    }
    
    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importData(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @QueryParam("token") String token,
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @Context UriInfo uriInfo) throws URISyntaxException {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        Scanner scanner = null;
        try {
            scanner = new Scanner(uploadedInputStream);
            scanner.useDelimiter("\\A");
            String data = scanner.hasNext() ? scanner.next() : "";
            DAOManager.getInstance().importData(data);
        }
        finally {
            if(scanner != null) {
                scanner.close();
            }
        }
        
        return APIResultUtils.buildURLResponse(request, "jsp/database.jsp");
    }
    
    @POST
    @Path("/save_config")
    @Produces("application/json")
    public Response saveConfig(
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
        
        try {
            ConfigManager.getInstance().saveOverrideConfig(content);
            
            Event event = new Event();
            event.type = EventType.ConfigOverrideUploaded;
            EventManager.getInstance().fireEvent(event);
            
            APIResult ret = APIResultUtils.buildOKAPIResult("Configuration override saved successfully.");
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to save config override!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Failed to save config override."));
        }
    }
    
    @GET
    @Path("/dump_heap")
    public Response dumpHeap(
            @QueryParam("token") String token,
            @Context HttpHeaders headers,
            @Context HttpServletRequest request,
            @Context UriInfo uriInfo)
    {
        String user_token = token;
        if(StringUtils.isBlank(user_token)) {
            user_token = (String)request.getSession().getAttribute("world-token");
        }
        if(!SecManager.getInstance().isValidToken(user_token)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Authentication is needed."));
        }
        
        final String fileName = ConfigManager.getInstance().getDataDir() + "heap.bin";
        ConsoleManager.getInstance().dumpHeap(fileName);
        
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    String url = fileName;
                    java.nio.file.Path path = Paths.get(url);
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    logger.error("failed to download heap dump", e);
                    throw new WebApplicationException();
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = heap.bin")
                .build();
    }
    
    @GET
    @Path("/listFiles")
    @Produces("application/json")
    public Response listFiles(
            @QueryParam("path") String path,
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
            List<FileInfo> infos = ConsoleManager.getInstance().listFiles(path);
            
            APIResult result = APIResultUtils.buildOKAPIResult("Files have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to list files", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/trend_memory")
    @Produces("application/json")
    public Response trendMemory(
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
        
        TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
        List<MemoryInfo> infos = ConsoleManager.getInstance().getMemoryTrend();
        Map<String, Integer> data = new HashMap<String, Integer>();
        List<String> keys = new ArrayList<String>();
        for(MemoryInfo info : infos) {
            String key = TimeUtils.getDateTimeUTCString(info.time, tz);
            data.put(key, info.percentage);
            keys.add(key);
        }
        
        StringBuffer sb = new StringBuffer("[");
        for(String key : keys) {
            sb.append("[");
            sb.append(key);
            sb.append(",");
            sb.append(data.get(key));
            sb.append("],");
        }
        sb.append("]");
        
        APIResult result = APIResultUtils.buildOKAPIResult(sb.toString());
        
        return APIResultUtils.buildJSONResponse(result);
    }
    
    @GET
    @Path("/list_object_graph")
    @Produces("application/json")
    public Response listObjectGraph(
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
            List<ObjectGraphInfo> infos = ConsoleManager.getInstance().getObjectGraphInfos();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Object graphs have been successfully fetched.");
            result.list = infos;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to list object graphs", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/trend_traffic")
    @Produces("application/json")
    public Response trendTraffic(
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
        
        TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
        List<RequestInfo> infos = ConsoleManager.getInstance().getRequestInfos();
        Map<String, Integer> data = new HashMap<String, Integer>();
        List<String> keys = new ArrayList<String>();
        for(RequestInfo info : infos) {
            String key = TimeUtils.getDateHourUTCString(info.time, tz);
            if(data.containsKey(key)) {
                int count = data.get(key);
                count += 1;
                data.put(key, count);
            }
            else {
                data.put(key, 1);
                keys.add(key);
            }
        }
        
        StringBuffer sb = new StringBuffer("[");
        for(String key : keys) {
            sb.append("[");
            sb.append(key);
            sb.append(",");
            sb.append(data.get(key));
            sb.append("],");
        }
        sb.append("]");
        
        APIResult result = APIResultUtils.buildOKAPIResult(sb.toString());
        
        return APIResultUtils.buildJSONResponse(result);
    }
    
    @GET
    @Path("/listRequests")
    @Produces("application/json")
    public Response listRequests(
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
            List<RequestStats> requests = ConsoleManager.getInstance().getRequestStats();
            
            APIResult result = APIResultUtils.buildOKAPIResult("Requests have been successfully fetched.");
            result.list = requests;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to list requests", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @GET
    @Path("/delete_file")
    @Produces("application/json")
    public Response deleteFile(
            @QueryParam("path") String path,
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
            String result = ConsoleManager.getInstance().deleteFile(path);
            APIResult ret = APIResultUtils.buildOKAPIResult(result);
            return APIResultUtils.buildJSONResponse(ret);
        }
        catch(Exception e) {
            logger.error("failed to delete file!", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/describe")
    @Produces("application/json")
    public Response describe(
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
        
        if(StringUtils.isBlank(name)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Name is needed."));
        }
        name = name.trim();
        
        try {
            ObjectInfo info = ScriptManager.getInstance().describe(name);
            String ret = "";
            if(info != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table table-striped table-bordered\"><thead><tr><th>Field Type</th><th>Field Name</th></tr></thead><tbody>");
                for(FieldInfo fieldInfo : info.fields) {
                    sb.append("<tr><td>" + fieldInfo.type + "</td><td>" + fieldInfo.name + "</td></tr>");
                }
                sb.append("</tbody></table>");
                
                sb.append("<table class=\"table table-striped table-bordered\"><thead><tr><th>Method Return Type</th><th>Method Name</th><th>Method Arg Types</th></tr></thead><tbody>");
                for(MethodInfo methodInfo : info.methods) {
                    sb.append("<tr><td>" + methodInfo.returnType + "</td><td>" + methodInfo.name + "</td><td>");
                    for(int i = 0; i < methodInfo.argTypes.length; i++) {
                        sb.append(methodInfo.argTypes[i]);
                        if(i != methodInfo.argTypes.length - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("</td></tr>");
                }
                sb.append("</tbody></table>");
                
                ret = sb.toString();
            }
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult(String.valueOf(ret)));
        }
        catch(Exception e) {
            logger.error("failed to describe object", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

package org.wilson.world.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.ScriptManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.QueryResult;

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
        
        return APIResultUtils.buildURLResponse(request, "config.jsp");
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
    public Response downloadPdfFile(
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
}

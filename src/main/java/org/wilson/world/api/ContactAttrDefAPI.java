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
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.ContactAttrDefManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.ContactAttrDef;

@Path("contact_attr_def")
public class ContactAttrDefAPI {
    private static final Logger logger = Logger.getLogger(ContactAttrDefAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def description should be provided."));
        }
        description = description.trim();
        
        try {
            ContactAttrDef def = new ContactAttrDef();
            def.name = name;
            def.type = type;
            def.description = description;
            ContactAttrDefManager.getInstance().createContactAttrDef(def);
            
            Event event = new Event();
            event.type = EventType.CreateContactAttrDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact attr def has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create contact attr def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
    
    @POST
    @Path("/update")
    @Produces("application/json")
    public Response update(
            @FormParam("id") int id,
            @FormParam("name") String name, 
            @FormParam("type") String type,
            @FormParam("description") String description,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(type)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def type should be provided."));
        }
        type = type.trim();
        if(StringUtils.isBlank(description)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def description should be provided."));
        }
        description = description.trim();
        
        try {
            ContactAttrDef oldDef = ContactAttrDefManager.getInstance().getContactAttrDef(id);
            
            ContactAttrDef def = new ContactAttrDef();
            def.id = id;
            def.name = name;
            def.type = type;
            def.description = description;
            ContactAttrDefManager.getInstance().updateContactAttrDef(def);
            
            Event event = new Event();
            event.type = EventType.UpdateContactAttrDef;
            event.data.put("old_data", oldDef);
            event.data.put("new_data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact attr def has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update contact attr def", e);
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
            ContactAttrDef def = ContactAttrDefManager.getInstance().getContactAttrDef(id);
            if(def != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Contact attr def has been successfully fetched.");
                result.data = def;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr def does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get contact attr def", e);
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
            List<ContactAttrDef> defs = ContactAttrDefManager.getInstance().getContactAttrDefs();
            
            Collections.sort(defs, new Comparator<ContactAttrDef>(){
                @Override
                public int compare(ContactAttrDef o1, ContactAttrDef o2) {
                    if(o1.id < 0 && o2.id < 0) {
                        return (-o1.id) - (-o2.id);
                    }
                    else {
                        return o1.id - o2.id;
                    }
                }
            });
            
            APIResult result = APIResultUtils.buildOKAPIResult("Contact attr defs have been successfully fetched.");
            result.list = defs;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get contact attr defs", e);
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
            ContactAttrDef def = ContactAttrDefManager.getInstance().getContactAttrDef(id);
            if(ContactAttrDefManager.getInstance().isContactAttrDefUsed(def.name)) {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact attr is being used."));
            }
            
            ContactAttrDefManager.getInstance().deleteContactAttrDef(id);
            
            Event event = new Event();
            event.type = EventType.DeleteContactAttrDef;
            event.data.put("data", def);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact attr def has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete contact attr def", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

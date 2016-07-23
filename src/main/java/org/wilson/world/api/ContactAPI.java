package org.wilson.world.api;

import java.util.ArrayList;
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
import org.wilson.world.manager.ContactManager;
import org.wilson.world.manager.EventManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.model.APIResult;
import org.wilson.world.model.Contact;
import org.wilson.world.model.ContactAttr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("contact")
public class ContactAPI {
    private static final Logger logger = Logger.getLogger(ContactAPI.class);
    
    @POST
    @Path("/create")
    @Produces("application/json")
    public Response create(
            @FormParam("name") String name, 
            @FormParam("content") String content,
            @FormParam("attrs") String attrs,
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact content should be provided."));
        }
        content = content.trim();
        
        try {
            Contact contact = new Contact();
            contact.name = name;
            contact.content = content;
            long createdTime = System.currentTimeMillis();
            contact.createdTime = createdTime;
            contact.modifiedTime = createdTime;
            
            if(!StringUtils.isBlank(attrs)) {
                JSONArray attrArray = JSONArray.fromObject(attrs);
                List<ContactAttr> attrList = new ArrayList<ContactAttr>();
                for(int i = 0; i < attrArray.size(); i++) {
                    JSONObject attrObj = attrArray.getJSONObject(i);
                    String p_name = attrObj.getString("name");
                    String p_value = attrObj.getString("value");
                    ContactAttr attr = new ContactAttr();
                    attr.name = p_name.trim();
                    attr.value = p_value.trim();
                    attrList.add(attr);
                }
                contact.attrs = attrList;
            }
            
            ContactManager.getInstance().createContact(contact);
            
            Event event = new Event();
            event.type = EventType.CreateContact;
            event.data.put("data", contact);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact has been successfully created."));
        }
        catch(Exception e) {
            logger.error("failed to create contact", e);
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
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact name should be provided."));
        }
        name = name.trim();
        if(StringUtils.isBlank(content)) {
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact content should be provided."));
        }
        content = content.trim();
        
        try {
            Contact oldcontact = ContactManager.getInstance().getContact(id);
            
            Contact contact = new Contact();
            contact.id = id;
            contact.name = name;
            contact.content = content;
            contact.modifiedTime = System.currentTimeMillis();
            
            if(!StringUtils.isBlank(attrs)) {
                JSONArray attrArray = JSONArray.fromObject(attrs);
                List<ContactAttr> attrList = new ArrayList<ContactAttr>();
                for(int i = 0; i < attrArray.size(); i++) {
                    JSONObject attrObj = attrArray.getJSONObject(i);
                    int p_id = attrObj.getInt("id");
                    String p_name = attrObj.getString("name");
                    String p_value = attrObj.getString("value");
                    ContactAttr attr = new ContactAttr();
                    attr.id = p_id;
                    attr.name = p_name.trim();
                    attr.value = p_value.trim();
                    attrList.add(attr);
                }
                contact.attrs = attrList;
            }
            
            ContactManager.getInstance().updateContact(contact);
            
            Event event = new Event();
            event.type = EventType.UpdateContact;
            event.data.put("old_data", oldcontact);
            event.data.put("new_data", contact);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact has been successfully updated."));
        }
        catch(Exception e) {
            logger.error("failed to update contact", e);
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
            Contact contact = ContactManager.getInstance().getContact(id);
            if(contact != null) {
                APIResult result = APIResultUtils.buildOKAPIResult("Contact has been successfully fetched.");
                result.data = contact;
                return APIResultUtils.buildJSONResponse(result);
            }
            else {
                return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult("Contact does not exist."));
            }
        }
        catch(Exception e) {
            logger.error("failed to get contact", e);
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
            List<Contact> contacts = ContactManager.getInstance().getContacts();
            
            APIResult result = APIResultUtils.buildOKAPIResult("contacts have been successfully fetched.");
            result.list = contacts;
            return APIResultUtils.buildJSONResponse(result);
        }
        catch(Exception e) {
            logger.error("failed to get contacts", e);
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
            Contact contact = ContactManager.getInstance().getContact(id);
            
            ContactManager.getInstance().deleteContact(id);
            
            Event event = new Event();
            event.type = EventType.DeleteContact;
            event.data.put("data", contact);
            EventManager.getInstance().fireEvent(event);
            
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("Contact has been successfully deleted."));
        }
        catch(Exception e) {
            logger.error("failed to delete contact", e);
            return APIResultUtils.buildJSONResponse(APIResultUtils.buildErrorAPIResult(e.getMessage()));
        }
    }
}

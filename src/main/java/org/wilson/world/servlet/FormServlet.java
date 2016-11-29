package org.wilson.world.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.wilson.world.form.Form;
import org.wilson.world.form.FormData;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.FormManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.SecManager;
import org.wilson.world.manager.URLManager;

public class FormServlet extends HttpServlet{
	private static final Logger logger = Logger.getLogger(FormServlet.class);
	
    private static final long serialVersionUID = 1L;
    
    private ServletFileUpload upload;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String user_token = (String)request.getSession().getAttribute("world-token");
        boolean isValidUser = SecManager.getInstance().isValidToken(user_token);
        if(!isValidUser) {
            return;
        }
    	
    	Form form = null;
    	try {
    		int id = Integer.parseInt(request.getParameter("id"));
    		form = FormManager.getInstance().getForm(id);
    	}
    	catch(Exception e) {
    		logger.error(e);
    	}
    	
    	String backUrl = URLManager.getInstance().getLastUrl();
    	if(form == null) {
    		NotifyManager.getInstance().notifyDanger("No such form could be found.");
    		response.sendRedirect(backUrl);
    	}
    	
    	FormData data = new FormData();
    	
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart) {
        	File uploadDir = new File(ConfigManager.getInstance().getUploadDataDir());
        	if(!uploadDir.exists()) {
        		uploadDir.mkdirs();
        	}
        	
        	try {
				List<FileItem> items = this.getServletFileUpload().parseRequest(request);
				
				for(FileItem item : items) {
					if(item.isFormField()) {
						String name = item.getFieldName();
						String value = item.getString();
						data.put(name, value);
					}
					else {
						String name = item.getFieldName();
						String fileName = item.getName();
						File file = new File(uploadDir, fileName);
						item.write(file);
						data.put(name, file);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
        }
        else {
        	Map<String, String []> parameters = request.getParameterMap();
        	for(Entry<String, String []> entry : parameters.entrySet()) {
        		String key = entry.getKey();
        		String [] value = entry.getValue();
        		if(value != null) {
        			data.put(key, value[0]);
        		}
        	}
        }
        
        String ret = form.execute(data);
        if(ret != null) {
        	NotifyManager.getInstance().notifySuccess(ret);
        }
        
        response.sendRedirect(backUrl);
    }
    
    private ServletFileUpload getServletFileUpload() {
    	if(upload == null) {
    		DiskFileItemFactory factory = new DiskFileItemFactory();
        	
        	File tmpDir = new File(ConfigManager.getInstance().getTmpDataDir());
        	if(!tmpDir.exists()) {
        		tmpDir.mkdirs();
        	}
        	factory.setRepository(tmpDir);
        	
        	upload = new ServletFileUpload(factory);
    	}
    	
    	return upload;
    }
}

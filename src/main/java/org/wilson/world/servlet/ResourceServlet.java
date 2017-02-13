package org.wilson.world.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.image.ImageRef;
import org.wilson.world.manager.ImageManager;

public class ResourceServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getParameter("type");
    	if(StringUtils.isBlank(type)) {
    		type = "jpeg";
    	}
        response.setContentType(type);
        
        InputStream is = null;
        String name = request.getParameter("name");
        if(StringUtils.isNotBlank(name)) {
            ImageRef ref = ImageManager.getInstance().getImageRef(name);
            if(ref != null) {
            	String url = ref.getUrl();
            	String widthStr = request.getParameter("width");
            	String heightStr = request.getParameter("height");
            	if(StringUtils.isNotBlank(widthStr) && StringUtils.isNotBlank(heightStr)) {
            		int width = -1;
            		int height = -1;
            		try {
            			width = Integer.parseInt(widthStr);
            			height = Integer.parseInt(heightStr);
            		}
            		catch(Exception e) {
            		}
            		
            		if(width > 0 && height > 0) {
            			url = ref.getUrl(url, width, height, true);
            		}
            	}
            	
            	if(StringUtils.isNotBlank(url)) {
            		URLConnection con = new URL(url).openConnection();
            		is = con.getInputStream();
            	}
            }
        }
        
        if(is == null) {
        	is = this.getClass().getClassLoader().getResourceAsStream("image_not_found.png");
        }
        
        ServletOutputStream out = response.getOutputStream();
        
        BufferedInputStream bin = new BufferedInputStream(is);  
        BufferedOutputStream bout = new BufferedOutputStream(out);  
        int ch =0; ;  
        while((ch=bin.read())!=-1)  
        {  
            bout.write(ch);  
        }  
          
        bin.close();  
        bout.close();  
    }
}

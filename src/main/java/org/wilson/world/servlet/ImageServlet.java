package org.wilson.world.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.SecManager;

public class ImageServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/jpeg");
        
        boolean showImage = true;
        
        String user_token = (String)request.getSession().getAttribute("world-token");
        boolean isValidUser = SecManager.getInstance().isValidToken(user_token);
        if(!isValidUser) {
            showImage = false;
        }
        
        String path = request.getParameter("path");
        if(StringUtils.isBlank(path)) {
            showImage = false;
        }
        else {
            path = path.trim();
        }
        
        ServletOutputStream out = response.getOutputStream();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("image_not_found.png");
        
        if(showImage) {
            File imageFile = new File(ConfigManager.getInstance().getDataDir() + path);
            if(imageFile.exists()) {
                is = new FileInputStream(imageFile);
            }
        }
          
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

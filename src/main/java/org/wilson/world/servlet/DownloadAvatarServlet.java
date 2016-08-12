package org.wilson.world.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.SecManager;

public class DownloadAvatarServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DownloadAvatarServlet.class);
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user_token = (String)req.getSession().getAttribute("world-token");
        boolean isValidUser = SecManager.getInstance().isValidToken(user_token);
        if(!isValidUser) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String filename = req.getParameter("filename");
        String filetype = req.getParameter("filetype");
        
        if(logger.isTraceEnabled()) {
            logger.trace("file name is " + filename);
        }
        
        if("svg".equals(filetype)) {
            filetype = "image/svg+xml";
        }
        else if("png".equals(filetype)) {
            filetype = "image/png";
        }
        resp.setContentType(filetype);

        resp.setHeader("Content-disposition","attachment; filename=" + filename);

        OutputStream out = resp.getOutputStream();
        InputStream in = new FileInputStream(ConfigManager.getInstance().getDataDir() + "avatar");
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > -1){
           out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
    }

}

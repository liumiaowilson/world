package org.wilson.world.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import sun.misc.BASE64Decoder;

public class SaveAvatarServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SaveAvatarServlet.class);
    
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user_token = (String)req.getSession().getAttribute("world-token");
        boolean isValidUser = SecManager.getInstance().isValidToken(user_token);
        if(!isValidUser) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String imgdata = req.getParameter("imgdata");
        String filename = req.getParameter("filename");
        
        if(logger.isTraceEnabled()) {
            logger.trace("file name is " + filename);
        }
        
        File file = new File(ConfigManager.getInstance().getDataDir() + "avatar");
        OutputStream out = new FileOutputStream(file);
        
        InputStream in = null;
        int pos = imgdata.indexOf(",");
        String prefix = imgdata.substring(0, pos);
        if(prefix.equals("data:image/png;base64")) {
            imgdata = imgdata.substring(pos + 1);
            BASE64Decoder decoder = new BASE64Decoder();
            in = new ByteArrayInputStream(decoder.decodeBuffer(imgdata));
        }
        else {
            in = new ByteArrayInputStream(imgdata.getBytes());
        }
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > -1){
           out.write(buffer, 0, length);
        }
        out.flush();
        in.close();
        out.close();
        
        resp.getWriter().print("saved");
    }

}

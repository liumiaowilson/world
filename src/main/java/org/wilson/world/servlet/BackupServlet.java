package org.wilson.world.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.BackupManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.SecManager;

public class BackupServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(BackupServlet.class);

    private List<File> getAllFiles(File file) {
    	List<File> files = new ArrayList<File>();
    	for(File f : file.listFiles()) {
    		if(f.isDirectory()) {
    			files.addAll(this.getAllFiles(f));
    		}
    		else {
    			files.add(f);
    		}
    	}
    	
    	return files;
    }
    
    private String getPath(File file, String root) {
    	String absPath = file.getAbsolutePath();
    	String path = absPath.substring(root.length());
    	if(!path.startsWith("/")) {
    		path = "/" + path;
    	}
    	
    	return path;
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		String user_token = (String)request.getSession().getAttribute("world-token");
            boolean isValidUser = SecManager.getInstance().isValidToken(user_token);
            if(!isValidUser) {
                return;
            }
    		
            String dir = ConfigManager.getInstance().getDataDir();
            if(StringUtils.isBlank(dir)) {
            	dir = new File(".").getAbsolutePath();
            }
            File dirFile = new File(dir);
            List<File> files = this.getAllFiles(dirFile);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            byte [] bytes = new byte[2048];
            for(File file : files) {
            	String path = this.getPath(file, dir);
            	if(BackupManager.getInstance().accept(path)) {
            		FileInputStream fis = new FileInputStream(file);
            		BufferedInputStream bis = new BufferedInputStream(fis);
            		
            		if(path.startsWith("/")) {
            			path = path.substring(1);
            		}
            		zos.putNextEntry(new ZipEntry(path));
            		int bytesRead;
                    while ((bytesRead = bis.read(bytes)) != -1) {
                        zos.write(bytes, 0, bytesRead);
                    }
                    zos.closeEntry();
                    
                    bis.close();
                    fis.close();
            	}
            }
            
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
            
            byte [] zip = baos.toByteArray();

            ServletOutputStream sos = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=Data.zip");

            sos.write(zip);
            sos.flush();
        }
        catch (Exception e) {
            logger.error(e);
        }
    }
}

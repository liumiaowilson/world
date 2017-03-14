package org.wilson.world.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.manager.ServletManager;

public class ExtServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        List<ActiveServlet> servlets = ServletManager.getInstance().getMatchingActiveServlets(url);
        if(!servlets.isEmpty()) {
        	ActiveServlet servlet = servlets.get(0);
            servlet.setContainer(this);
        	servlet.doGet(req, resp);
        }
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String url = req.getRequestURI();
        List<ActiveServlet> servlets = ServletManager.getInstance().getMatchingActiveServlets(url);
        if(!servlets.isEmpty()) {
        	ActiveServlet servlet = servlets.get(0);
            servlet.setContainer(this);
        	servlet.doPost(req, resp);
        }
    }
}

package org.wilson.world.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.PageManager;
import org.wilson.world.manager.TemplateManager;
import org.wilson.world.model.Page;
import org.wilson.world.servlet.ControllerServlet;

public class PageRedirector {
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public void put(String name, Object value) {
		this.data.put(name, value);
	}
	
	public Object get(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.data.get(name);
	}
	
	public Object remove(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.data.remove(name);
	}
	
	public void redirect(HttpServletResponse response, String pageName) throws IOException {
		if(response == null) {
			return;
		}
		if(StringUtils.isBlank(pageName)) {
			ControllerServlet.jumpToHomePage(response, "No page name could be found.");
			return;
		}
		
		Page page = PageManager.getInstance().getPage(pageName);
		if(page == null) {
			ControllerServlet.jumpToHomePage(response, "No page could be found.");
			return;
		}
		
		String content = TemplateManager.getInstance().evaluate(page.content, this.data);
		PrintWriter pw = response.getWriter();
		pw.print(content);
		pw.flush();
	}
}

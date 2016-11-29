package org.wilson.world.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.PageManager;
import org.wilson.world.manager.TemplateManager;
import org.wilson.world.model.Page;
import org.wilson.world.servlet.ControllerServlet;

public class PageRedirector {
	private Map<String, Object> data = new HashMap<String, Object>();
	
	private List<String> scripts = new ArrayList<String>();
	private List<String> styles = new ArrayList<String>();
	private boolean isNested;
	private String pageTitle;
	//use another page to introduce scripts
	private String scriptPageName;
	
	public void setScriptPageName(String scriptPageName) {
		this.scriptPageName = scriptPageName;
	}
	
	public String getScriptPageName() {
		return this.scriptPageName;
	}
	
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	public String getPageTitle() {
		return this.pageTitle;
	}
	
	public void addScript(String script) {
		this.scripts.add(script);
	}
	
	public void removeScript(String script) {
		this.scripts.remove(script);
	}
	
	public List<String> getScripts() {
		return this.scripts;
	}
	
	public void addStyle(String style) {
		this.styles.add(style);
	}
	
	public void removeStyle(String style) {
		this.styles.remove(style);
	}
	
	public List<String> getStyles() {
		return this.styles;
	}
	
	public boolean isNested() {
		return this.isNested;
	}
	
	public void setNested(boolean isNested) {
		this.isNested = isNested;
	}
	
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
	
	public void redirect(HttpServletRequest request, HttpServletResponse response, String pageName) throws IOException {
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
		
		if(!this.isNested()) {
			String content = this.getPageContent(page);
			PrintWriter pw = response.getWriter();
			pw.print(content);
			pw.flush();
		}
		else {
			request.getSession().setAttribute("world-page_redirector", this);
			response.sendRedirect("/jsp/page_load.jsp?id=" + page.id);
		}
	}
	
	public String getPageContent(Page page) {
		if(page == null) {
			return null;
		}
		
		String content = TemplateManager.getInstance().evaluate(page.content, this.data);
		return content;
	}
}

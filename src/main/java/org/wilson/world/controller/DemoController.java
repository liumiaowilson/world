package org.wilson.world.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.manager.NotifyManager;

public class DemoController extends AbstractController {
	public DemoController() {
		super();
		this.setName("demo");
		this.setSecure(true);
	}
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response, PageRedirector redirector) throws Exception {
		NotifyManager.getInstance().notifySuccess("Demo controller executed");
		
		return null;
	}

}

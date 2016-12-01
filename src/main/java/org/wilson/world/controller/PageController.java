package org.wilson.world.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.model.Page;

public class PageController extends AbstractController {
	private Page page;
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response, PageRedirector redirector)
			throws Exception {
		redirector.setNested(true);
		redirector.setPageTitle(page.name);
		
		return this.page.name;
	}

}

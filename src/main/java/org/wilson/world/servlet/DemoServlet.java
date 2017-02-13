package org.wilson.world.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoServlet extends ActiveServlet {

	private static final long serialVersionUID = 7502090894392003895L;

	@Override
	public String getName() {
		return "demo";
	}

	@Override
	public String getPattern() {
		return "/servlet/ext/demo";
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("Hello World");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("Hello World");
	}

}

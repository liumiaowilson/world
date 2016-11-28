package org.wilson.world.controller;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic system controller", name = "system.controller")
public abstract class AbstractController implements Controller {
	private int id;
	private String name;
	private boolean isSecure = true;
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}

	@Override
	public boolean isSecure() {
		return this.isSecure;
	}

	@Override
	public String getUri() {
		if(this.isSecure()) {
			return "/jsp_ext/" + this.name;
		}
		else {
			return "/public_ext/" + this.name;
		}
	}
}

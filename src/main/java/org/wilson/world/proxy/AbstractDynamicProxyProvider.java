package org.wilson.world.proxy;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic proxy provider", name = "system.proxy")
public abstract class AbstractDynamicProxyProvider implements DynamicProxyProvider {
	private int id;
	private String name;
	
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
}

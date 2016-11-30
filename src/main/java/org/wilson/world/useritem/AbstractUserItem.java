package org.wilson.world.useritem;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic user items", name = "system.useritem")
public abstract class AbstractUserItem implements UserItem {
	private int id;
	private String name;
	private String type;
	private String description;
	private int value;
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getValue() {
		return this.value;
	}

	protected void setValue(int value) {
		this.value = value;
	}
}

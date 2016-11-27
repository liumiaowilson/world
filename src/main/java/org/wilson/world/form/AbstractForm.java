package org.wilson.world.form;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic system form", name = "system.form")
public abstract class AbstractForm implements Form {
	private int id;
	private String name;
	private String description;
	private Inputs inputs;
	
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

	@Override
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Inputs getInputs() {
		if(inputs == null) {
			inputs = new Inputs();
			inputs = this.buildInputs(inputs);
		}
		return this.inputs;
	}

	public abstract Inputs buildInputs(Inputs inputs);
}

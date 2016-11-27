package org.wilson.world.form;

import java.util.ArrayList;
import java.util.List;

public class Inputs {
	private List<Input> inputs = new ArrayList<Input>();
	
	public Inputs() {
	}
	
	public void addInput(Input input) {
		if(input != null) {
			inputs.add(input);
		}
	}
	
	public void removeInput(Input input) {
		if(input != null) {
			inputs.remove(input);
		}
	}
	
	public List<Input> getAll() {
		return this.inputs;
	}
	
	public boolean hasFileInput() {
		for(Input input : this.inputs) {
			if(InputType.File == input.getType()) {
				return true;
			}
		}
		
		return false;
	}
}

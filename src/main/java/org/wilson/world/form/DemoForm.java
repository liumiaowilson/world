package org.wilson.world.form;

import java.io.File;

public class DemoForm extends AbstractForm {
	public DemoForm() {
		super();
		this.setName("demo");
		this.setTitle("Demo");
		this.setDescription("Demo for how to use generic forms");
	}
	
	@Override
	public String execute(FormData data) {
		String name = data.getString("name");
		File resumeFile = data.getFile("resume");
		System.out.println("Name is " + name);
		System.out.println("File is " + resumeFile);
		
		return "Form has been successfully executed.";
	}

	@Override
	public Inputs buildInputs(Inputs inputs) {
		inputs.addInput(new Input("name", "Name", InputType.String, true, "Your name"));
		inputs.addInput(new Input("resume", "Resume", InputType.File, "Your resume"));
		return inputs;
	}

}

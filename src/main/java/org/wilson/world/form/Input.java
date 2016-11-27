package org.wilson.world.form;

public class Input {
	private String	  name;
	private String	  label;
	private InputType type;
	private String[]  values;
	private boolean	  mandatory;
	private String	  description;

	public Input(String name, String label, InputType type, String[] values, boolean mandatory, String description) {
		this.name = name;
		this.label = label;
		this.type = type;
		this.values = values;
		this.mandatory = mandatory;
		this.description = description;
	}

	public Input(String name, String label, InputType type, boolean mandatory, String description) {
		this(name, label, type, null, mandatory, description);
	}

	public Input(String name, String label, InputType type, String description) {
		this(name, label, type, false, description);
	}

	public Input(String name, String label, InputType type) {
		this(name, label, type, null);
	}

	public Input(String name, String label) {
		this(name, label, InputType.String);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InputType getType() {
		return type;
	}

	public void setType(InputType type) {
		this.type = type;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Input other = (Input) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

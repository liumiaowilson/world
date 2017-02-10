package org.wilson.world.endpoint;

public class EndPointMethod {
	private String				name;
	private EndPointMethodType	type;
	private EndPointMethodScope	scope;

	public EndPointMethod(String name, EndPointMethodType type, EndPointMethodScope scope) {
		super();
		this.name = name;
		this.type = type;
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EndPointMethodType getType() {
		return type;
	}

	public void setType(EndPointMethodType type) {
		this.type = type;
	}

	public EndPointMethodScope getScope() {
		return scope;
	}

	public void setScope(EndPointMethodScope scope) {
		this.scope = scope;
	}
}

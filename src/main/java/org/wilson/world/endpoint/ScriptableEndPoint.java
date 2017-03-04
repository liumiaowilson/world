package org.wilson.world.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.wilson.world.java.Script;
import org.wilson.world.java.Scriptable;

public abstract class ScriptableEndPoint extends EndPoint implements Scriptable {
	protected Script script = null;
	private List<String> methodNames = new ArrayList<String>();
	
	public ScriptableEndPoint() {
		
	}
	
	public void add(String name, String type, String scope) {
		if(!this.methodNames.contains(name)) {
			addMethod(name, this.getType(type), this.getScope(scope));
			this.methodNames.add(name);
		}
	}
	
	public void loadMethods() {
		if(script != null) {
			script.invoke("loadMethods");
		}
	}

	@Override
	public void setScript(Script script) {
		this.script = script;
		
		this.loadMethods();
	}
	
	public Response handle(EndPointMethod method, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo)
			throws Exception {
		return (Response) script.invoke(method.getName(), headers, request, uriInfo);
	}
}

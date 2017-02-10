package org.wilson.world.endpoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.JavaExtensible;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.server.impl.application.WebApplicationContext;

/**
 * Abstract class for dynamic restful APIs
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Generic API endpoint", name = "system.endpoint")
public abstract class EndPoint {
	private static final Logger logger = Logger.getLogger(EndPoint.class);
	
	private Map<String, EndPointMethod> methods = new HashMap<String, EndPointMethod>();
	private boolean loaded = false;
	private Map<String, Method> callMap = new HashMap<String, Method>();
	
	public EndPoint() {
		this.loadMethods();
	}
	
	public void addMethod(EndPointMethod method) {
		if(method != null && method.getName() != null) {
			methods.put(method.getName(), method);
		}
	}
	
	public void removeMethod(EndPointMethod method) {
		if(method != null && method.getName() != null) {
			methods.remove(method.getName());
		}
	}
	
	public void addMethod(String name, EndPointMethodType type, EndPointMethodScope scope) {
		this.addMethod(new EndPointMethod(name, type, scope));
	}
	
	/**
	 * Load end point methods
	 * 
	 */
	public void loadMethods() {
		try {
			if(!loaded) {
				Class<?> clazz = this.getClass();
				Method [] ms = clazz.getDeclaredMethods();
				for(Method m : ms) {
					EndPointMark mark = m.getAnnotation(EndPointMark.class);
					if(mark != null) {
						Class<?> [] parameterTypes = m.getParameterTypes();
						if(parameterTypes.length != 3 || parameterTypes[0] != HttpHeaders.class || parameterTypes[1] != HttpServletRequest.class || parameterTypes[2] != UriInfo.class) {
							continue;
						}
						
						Class<?> returnType = m.getReturnType();
						if(returnType != Response.class) {
							continue;
						}
						
						this.addMethod(mark.name(), mark.type(), mark.scope());
						this.callMap.put(mark.name(), m);
					}
				}
				
				loaded = true;
			}
		}
		catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Get the name of the end point
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Get end point methods
	 * 
	 * @return
	 */
	public List<EndPointMethod> getMethods() {
		return new ArrayList<EndPointMethod>(this.methods.values());
	}
	
	public Response handle(EndPointMethod method, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo)
			throws Exception {
		Method m = callMap.get(method.getName());
		if(m == null) {
			return null;
		}
		
		return (Response) m.invoke(this, headers, request, uriInfo);
	}
	
	public EndPointMethod getMethod(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.methods.get(name);
	}
	
	private Response execute(String methodName, EndPointMethodType type, EndPointMethodScope scope, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo) throws Exception {
		EndPointMethod method = this.getMethod(methodName);
		if(method == null) {
			return null;
		}
		
		if(type == method.getType() && scope == method.getScope()) {
			return this.handle(method, headers, request, uriInfo);
		}
		else {
			return null;
		}
	}
	
	public Response doGet(String methodName, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo) throws Exception {
		return this.execute(methodName, EndPointMethodType.GET, EndPointMethodScope.Private, headers, request, uriInfo);
	}
	
	public Response doPost(String methodName, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo) throws Exception {
		return this.execute(methodName, EndPointMethodType.POST, EndPointMethodScope.Private, headers, request, uriInfo);
	}
	
	public Response doGetPublic(String methodName, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo) throws Exception {
		return this.execute(methodName, EndPointMethodType.GET, EndPointMethodScope.Public, headers, request, uriInfo);
	}
	
	public Response doPostPublic(String methodName, HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo) throws Exception {
		return this.execute(methodName, EndPointMethodType.POST, EndPointMethodScope.Public, headers, request, uriInfo);
	}
	
	public Map<String, List<String>> getFormParameters(UriInfo uriInfo) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		if(uriInfo instanceof WebApplicationContext) {
			WebApplicationContext context = (WebApplicationContext) uriInfo;
			Form form = context.getRequest().getFormParameters();
			for(Entry<String, List<String>> entry : form.entrySet()) {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}
	
	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		if(uriInfo instanceof WebApplicationContext) {
			WebApplicationContext context = (WebApplicationContext) uriInfo;
			MultivaluedMap<String, String> params = context.getRequest().getQueryParameters();
			for(Entry<String, List<String>> entry : params.entrySet()) {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}
	
	public String getFormParameter(UriInfo uriInfo, String name) {
		List<String> params = this.getFormParameters(uriInfo, name);
		if(params == null || params.isEmpty()) {
			return null;
		}
		else {
			return params.get(0);
		}
	}
	
	public List<String> getFormParameters(UriInfo uriInfo, String name) {
		if(uriInfo instanceof WebApplicationContext) {
			WebApplicationContext context = (WebApplicationContext) uriInfo;
			Form form = context.getRequest().getFormParameters();
			return form.get(name);
		}
		
		return null;
	}
	
	public String getQueryParameter(UriInfo uriInfo, String name) {
		List<String> params = this.getQueryParameters(uriInfo, name);
		if(params == null || params.isEmpty()) {
			return null;
		}
		else {
			return params.get(0);
		}
	}
	
	public List<String> getQueryParameters(UriInfo uriInfo, String name) {
		if(uriInfo instanceof WebApplicationContext) {
			WebApplicationContext context = (WebApplicationContext) uriInfo;
			MultivaluedMap<String, String> params = context.getRequest().getQueryParameters();
			return params.get(name);
		}
		
		return null;
	}
}

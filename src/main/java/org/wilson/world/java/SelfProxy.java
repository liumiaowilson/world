package org.wilson.world.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.util.ClassUtils;

public class SelfProxy {
	private static final Logger logger = Logger.getLogger(SelfProxy.class);
	
	private Scriptable scriptable = null;
	private Map<String, Method> methods = new HashMap<String, Method>();
	
	public SelfProxy(Scriptable scriptable) {
		this.scriptable = scriptable;
		
		try {
			Class<?> clazz = this.scriptable.getClass();
			for(Method method : ClassUtils.getAllMethods(clazz)) {
				this.methods.put(method.getName(), method);
			}
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
	public List<String> getMethodNames() {
		return new ArrayList<String>(this.methods.keySet());
	}
	
	public Object invoke(String name, Object... args) throws Exception {
		Method method = this.methods.get(name);
		if(method == null) {
			throw new NoSuchMethodException("Method [" + name + "] is not in this object.");
		}
		
		method.setAccessible(true);
		return method.invoke(this.scriptable, args);
	}
	
	public Map<String, Object> genContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("_self", this);
		return context;
	}
	
	public String genScript() {
		StringBuilder sb = new StringBuilder();
		sb.append("var self = {};\n");
		for(Method method : this.methods.values()) {
			if(method.getDeclaringClass() == Object.class) {
				continue;
			}
			sb.append("self[\"" + method.getName() + "\"] = function(");
			int p_num = method.getParameterTypes().length;
			for(int i = 0; i < p_num; i++) {
				sb.append("p" + i);
				if(i != p_num - 1) {
					sb.append(", ");
				}
			}
			sb.append(") {\n");
			sb.append("    return _self.invoke(\"");
			sb.append(method.getName());
			sb.append("\"");
			for(int i = 0; i < p_num; i++) {
				sb.append(", p" + i);
			}
			sb.append(");\n");
			sb.append("}\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}

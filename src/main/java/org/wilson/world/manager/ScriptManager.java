package org.wilson.world.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.console.Log;
import org.wilson.world.exception.DataException;
import org.wilson.world.java.ActiveObject;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.java.Script;
import org.wilson.world.script.FieldInfo;
import org.wilson.world.script.MethodInfo;
import org.wilson.world.script.ObjectInfo;

public class ScriptManager implements JavaExtensionListener<ActiveObject>, ActiveManagerListener {
    private static final Logger logger = Logger.getLogger(ScriptManager.class);
    
    private static ScriptManager instance;
    
    private ScriptEngineManager sem;
    
    private Map<String, Object> bindings = new HashMap<String, Object>();
    
    private ScriptManager() {
    	ExtManager.getInstance().addJavaExtensionListener(this);
    	ManagerManager.getInstance().addActiveManagerListener(this);
    	
    	sem = new ScriptEngineManager();
    }
    
    public static ScriptManager getInstance() {
        if(instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }
    
    private ScriptEngine getEngine() {
    	return sem.getEngineByName("JavaScript");
    }
    
    public void removeBinding(String key) {
    	this.bindings.remove(key);
    }
    
    public void addBinding(String key, Object value) {
        this.bindings.put(key, value);
    }
    
    public void addBindings(Map<String, Object> bindings) {
        if(bindings != null) {
            this.bindings.putAll(bindings);
        }
    }
    
    public void notifyStarted() {
    	List<Object> managers = ManagerLoader.getManagers();
        for(Object manager : managers) {
            String name = manager.getClass().getSimpleName();
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            this.addBinding(name, manager);
        }
        
        this.loadSystemBindings();
    }
    
    private void loadSystemBindings() {
    	this.addBinding("log", Log.getInstance());
    }
    
    public Map<String, Object> getBindings() {
        return this.bindings;
    }
    
    public Object run(String script, Map<String, Object> context) {
        if(StringUtils.isBlank(script)) {
            return null;
        }
        
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        String systemScript = JsFileManager.getInstance().getSystemJavaScript();
        script = systemScript + script;
        try {
        	ScriptEngine engine = this.getEngine();
        	
        	for(Entry<String, Object> entry : this.getBindings().entrySet()) {
        		engine.put(entry.getKey(), entry.getValue());
        	}
            if(context != null) {
            	for(Entry<String, Object> entry : context.entrySet()) {
            		engine.put(entry.getKey(), entry.getValue());
            	}
            }
            
            Thread.currentThread().setContextClassLoader(JavaManager.getInstance().getClassLoader());
            return engine.eval(script);
        }
        catch(Exception e) {
            logger.error("failed to run script", e);
            String err = e.getMessage();
            if(e instanceof ScriptException) {
            	err = this.getErrMessage(script, (ScriptException) e);
            }
            throw new DataException(err);
        }
        finally {
        	Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
    
    public Object run(String script) {
        return run(script, null);
    }
    
    public Script eval(String script, Map<String, Object> context) {
    	if(StringUtils.isBlank(script)) {
            return null;
        }
        
    	ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    	String systemScript = JsFileManager.getInstance().getSystemJavaScript();
    	script = systemScript + script;
        try {
        	ScriptEngine engine = this.getEngine();
        	
        	for(Entry<String, Object> entry : this.getBindings().entrySet()) {
        		engine.put(entry.getKey(), entry.getValue());
        	}
            if(context != null) {
            	for(Entry<String, Object> entry : context.entrySet()) {
            		engine.put(entry.getKey(), entry.getValue());
            	}
            }
            
            Thread.currentThread().setContextClassLoader(JavaManager.getInstance().getClassLoader());
            engine.eval(script);
            
            Invocable inv = (Invocable) engine;
            return new Script(inv, script);
        }
        catch(Exception e) {
        	logger.error("failed to eval script", e);
            String err = e.getMessage();
            if(e instanceof ScriptException) {
            	err = this.getErrMessage(script, (ScriptException) e);
            }
            throw new DataException(err);
        }
        finally {
        	Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
    
    public Script eval(String script) {
    	return this.eval(script, null);
    }
    
    public String getErrMessage(String script, ScriptException se) {
    	if(StringUtils.isBlank(script) || se == null) {
    		return null;
    	}
    	
    	int lineNo = se.getLineNumber();
    	String [] lines = script.split("\n");
    	if(lineNo <= lines.length) {
    		return se.getMessage() + "\nLine is: " + lines[lineNo - 1] + "\nScript is: \n" + script;
    	}
    	else {
    		return se.getMessage();
    	}
    }
    
    @SuppressWarnings("rawtypes")
    public ObjectInfo describe(String name) throws Exception {
        Object obj = this.run(name);
        if(obj == null) {
            return null;
        }
        
        Class clazz = (obj instanceof Class ? (Class)obj : obj.getClass());
        ObjectInfo info = new ObjectInfo();
        
        Field [] fields = clazz.getFields();
        List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        for(Field field : fields) {
            if(Modifier.isPublic(field.getModifiers())) {
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.name = field.getName();
                fieldInfo.type = field.getType().getCanonicalName();
                fieldInfos.add(fieldInfo);
            }
        }
        info.fields = fieldInfos.toArray(new FieldInfo[0]);
        
        Method [] methods = clazz.getMethods();
        List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        for(Method method : methods) {
            if(Modifier.isPublic(method.getModifiers())) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.name = method.getName();
                methodInfo.returnType = method.getReturnType().getCanonicalName();
                Class [] parameterTypes = method.getParameterTypes();
                methodInfo.argTypes = new String [parameterTypes.length];
                for(int i = 0; i < parameterTypes.length; i++) {
                    methodInfo.argTypes[i] = parameterTypes[i].getCanonicalName();
                }
                methodInfos.add(methodInfo);
            }
        }
        info.methods = methodInfos.toArray(new MethodInfo[0]);
        
        return info;
    }

	@Override
	public Class<ActiveObject> getExtensionClass() {
		return ActiveObject.class;
	}

	@Override
	public void created(ActiveObject t) {
		if(t != null && StringUtils.isNotBlank(t.getName())) {
			this.addBinding(t.getName(), t);
		}
	}

	@Override
	public void removed(ActiveObject t) {
		if(t != null && StringUtils.isNotBlank(t.getName())) {
			this.removeBinding(t.getName());
		}
	}

	@Override
	public void created(ActiveManager manager) {
		if(manager != null && manager.getName() != null) {
			this.addBinding(manager.getName(), manager);
		}
	}

	@Override
	public void removed(ActiveManager manager) {
		if(manager != null && manager.getName() != null) {
			this.removeBinding(manager.getName());
		}
	}
}

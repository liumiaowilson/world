package org.wilson.world.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;
import org.wilson.world.script.FieldInfo;
import org.wilson.world.script.MethodInfo;
import org.wilson.world.script.ObjectInfo;

public class ScriptManager {
    private static final Logger logger = Logger.getLogger(ScriptManager.class);
    
    private static ScriptManager instance;
    
    private ScriptEngine engine;
    
    private ScriptManager() {
    }
    
    public static ScriptManager getInstance() {
        if(instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }
    
    private ScriptEngine getEngine() {
        if(this.engine == null) {
            ScriptEngineManager sem = new ScriptEngineManager();
            this.engine = sem.getEngineByName("JavaScript");
            List<Object> managers = ManagerLoader.getManagers();
            for(Object manager : managers) {
                String name = manager.getClass().getSimpleName();
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                this.engine.put(name, manager);
            }
        }
        return this.engine;
    }
    
    public void addBinding(String key, Object value) {
        this.getEngine().put(key, value);
    }
    
    public void addBindings(Map<String, Object> bindings) {
        if(bindings != null) {
            for(Entry<String, Object> entry : bindings.entrySet()) {
                this.getEngine().put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public Map<String, Object> getBindings() {
        return this.getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
    }
    
    public Object run(String script, Map<String, Object> context) {
        if(StringUtils.isBlank(script)) {
            return null;
        }
        
        Object ret = null;
        try {
            if(context != null) {
                Bindings bindings = this.getEngine().createBindings();
                for(Entry<String, Object> entry : context.entrySet()) {
                    bindings.put(entry.getKey(), entry.getValue());
                }
                ret = this.getEngine().eval(script, bindings);
            }
            else {
                ret = this.getEngine().eval(script);
            }
        }
        catch(Exception e) {
            logger.error("failed to run script", e);
            throw new DataException(e.getMessage());
        }
        return ret;
    }
    
    public Object run(String script) {
        return run(script, null);
    }
    
    @SuppressWarnings("rawtypes")
    public ObjectInfo describe(String name) throws Exception {
        Object obj = this.run(name);
        if(obj == null) {
            return null;
        }
        
        Class clazz = obj.getClass();
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
}

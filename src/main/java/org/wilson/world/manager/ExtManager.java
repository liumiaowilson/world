package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.character.Disaster;
import org.wilson.world.ext.ExtInvocationHandler;
import org.wilson.world.ext.ExtensionPoint;
import org.wilson.world.ext.Scriptable;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Action;

public class ExtManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(ExtManager.class);
    
    public static final String PREFIX = "ext.";
    
    private static ExtManager instance;
    
    private Map<String, Object> extensions = new HashMap<String, Object>();
    private Map<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
    
    private ExtManager() {
        this.loadExtensionPoints();
    }
    
    public static ExtManager getInstance() {
        if(instance == null) {
            instance = new ExtManager();
        }
        return instance;
    }
    
    public void loadExtensionPoints() {
    }
    
    public Object getExtension(String name) {
        return this.extensions.get(name);
    }
    
    @SuppressWarnings("rawtypes")
    public void addInterface(Class extensionClass) {
        if(extensionClass == null) {
            return;
        }
        if(!extensionClass.isInterface()) {
            return;
        }
        
        Method [] methods = extensionClass.getDeclaredMethods();
        if(methods.length != 1) {
            return;
        }
        Method extensionPoint = methods[0];
        Scriptable scriptable = extensionPoint.getAnnotation(Scriptable.class);
        if(scriptable != null) {
            ExtensionPoint ep = new ExtensionPoint();
            ep.name = scriptable.name();
            Class [] parameterTypes = extensionPoint.getParameterTypes();
            String [] names = scriptable.params();
            if(names.length != parameterTypes.length) {
                logger.warn("Scriptable annotation fails to match for [" + extensionClass.getCanonicalName() + "].");
                return;
            }
            for(int i = 0; i < names.length; i++) {
                String name = names[i];
                Class clazz = parameterTypes[i];
                ep.params.put(name, clazz);
            }
            this.extensionPoints.put(ep.name, ep);
            
            String actionName = DataManager.getInstance().getValue(PREFIX + ep.name);
            Action action = ActionManager.getInstance().getAction(actionName);
            if(action != null) {
                logger.info("Use action [" + actionName + "] for extension point [" + ep.name + "].");
                Object impl = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {extensionClass}, new ExtInvocationHandler(action));
                this.extensions.put(ep.name, impl);
            }
            else {
                logger.info("Use default impl for extension point [" + ep.name + "].");
                String implClassName = extensionClass.getCanonicalName() + "Impl";
                try {
                    Class implClass = Class.forName(implClassName);
                    Object impl = implClass.newInstance();
                    this.extensions.put(ep.name, impl);
                }
                catch(Exception e) {
                    logger.error("failed to set impl for extension point!", e);
                }
            }
        }
    }

    @Override
    public void start() {
        logger.info("Load extensions...");
        this.addInterface(Disaster.class);
    }

    @Override
    public void shutdown() {
    }
}

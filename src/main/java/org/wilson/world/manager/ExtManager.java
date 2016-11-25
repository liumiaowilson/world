package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.character.DeathExecution;
import org.wilson.world.character.Disaster;
import org.wilson.world.context.ContextInitializer;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.exception.DataException;
import org.wilson.world.ext.ExtInvocationHandler;
import org.wilson.world.ext.Scriptable;
import org.wilson.world.java.ActiveObject;
import org.wilson.world.java.JavaExtensible;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.java.JavaExtensionPoint;
import org.wilson.world.java.JavaObject;
import org.wilson.world.java.JavaObjectListener;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Action;
import org.wilson.world.model.ActionParam;
import org.wilson.world.model.ExtensionPoint;
import org.wilson.world.novel.NovelRoleDescriptor;
import org.wilson.world.novel.NovelRoleImageProvider;
import org.wilson.world.novel.NovelRoleReportBuilder;
import org.wilson.world.novel.NovelRoleValidator;
import org.wilson.world.query.QueryHandler;
import org.wilson.world.quiz.QuizProcessor;
import org.wilson.world.reward.RewardGiver;
import org.wilson.world.schedule.DefaultJob;
import org.wilson.world.skill.SkillCanTrigger;
import org.wilson.world.skill.SkillTrigger;
import org.wilson.world.status.StatusActivator;
import org.wilson.world.status.StatusDeactivator;
import org.wilson.world.task.TaskAttrComparator;
import org.wilson.world.task.TaskDefaultValueProvider;
import org.wilson.world.task.TaskFollowerAction;
import org.wilson.world.task.TaskSpawner;
import org.wilson.world.task.TaskTemplateEP;
import org.wilson.world.useritem.UserItemEffect;
import org.wilson.world.util.ObjectUtils;
import org.wilson.world.web.SystemWebJob;
import org.wilson.world.web.WebJobExecutor;

public class ExtManager implements ManagerLifecycle, EventListener, JavaObjectListener {
    private static final Logger logger = Logger.getLogger(ExtManager.class);
    
    private static ExtManager instance;
    
    private Map<String, Object> extensions = new HashMap<String, Object>();
    @SuppressWarnings("rawtypes")
    private Map<Class, Object> classExtensions = new HashMap<Class, Object>();
    @SuppressWarnings("rawtypes")
    private Map<Class, String> classExtensionNames = new HashMap<Class, String>();
    private Map<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
    private Map<String, JavaExtensionPoint> javaExtensionPoints = new HashMap<String, JavaExtensionPoint>();
    private Map<Integer, JavaObject> javaExtensions = new HashMap<Integer, JavaObject>();
    
    @SuppressWarnings("rawtypes")
	private List<JavaExtensionListener> listeners = new ArrayList<JavaExtensionListener>();
    
    private ExtManager() {
        EventManager.getInstance().registerListener(EventType.DeleteAction, this);
        JavaObjectManager.getInstance().addJavaObjectListener(this);
    }
    
    public static ExtManager getInstance() {
        if(instance == null) {
            instance = new ExtManager();
        }
        return instance;
    }
    
    public ExtensionPoint getExtensionPoint(String name) {
        return this.extensionPoints.get(name);
    }
    
    public List<ExtensionPoint> getExtensionPoints() {
        List<ExtensionPoint> eps = new ArrayList<ExtensionPoint>(this.extensionPoints.values());
        Collections.sort(eps, new Comparator<ExtensionPoint>(){

            @Override
            public int compare(ExtensionPoint o1, ExtensionPoint o2) {
                return o1.name.compareTo(o2.name);
            }
            
        });
        return eps;
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
            ep.description = scriptable.description();
            Class [] parameterTypes = extensionPoint.getParameterTypes();
            String [] names = scriptable.params();
            if(names.length != parameterTypes.length) {
                logger.warn("Scriptable annotation fails to match for [" + extensionClass.getCanonicalName() + "].");
                return;
            }
            for(int i = 0; i < names.length; i++) {
                String name = names[i];
                Class clazz = parameterTypes[i];
                ep.paramNames.add(name);
                ep.params.put(name, clazz);
            }
            
            Class returnType = extensionPoint.getReturnType();
            ep.returnType = returnType;
            
            this.extensionPoints.put(ep.name, ep);
            this.classExtensionNames.put(extensionClass, ep.name);
            
            Action action = this.getBoundAction(ep.name);
            if(action != null) {
                String actionName = action.name;
                logger.info("Use action [" + actionName + "] for extension point [" + ep.name + "].");
                Object impl = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {extensionClass}, new ExtInvocationHandler(action));
                this.extensions.put(ep.name, impl);
                this.classExtensions.put(extensionClass, impl);
            }
            else {
                logger.info("Use default impl for extension point [" + ep.name + "].");
                String implClassName = extensionClass.getCanonicalName() + "Impl";
                try {
                    Class implClass = Class.forName(implClassName);
                    Object impl = implClass.newInstance();
                    this.extensions.put(ep.name, impl);
                    this.classExtensions.put(extensionClass, impl);
                }
                catch(Exception e) {
                    logger.error("failed to set impl for extension point!", e);
                }
            }
            
            JavaExtensionPoint javaEP = new JavaExtensionPoint();
            javaEP.name = ep.name;
            javaEP.description = ep.description;
            javaEP.clazz = extensionClass;
            this.javaExtensionPoints.put(javaEP.name, javaEP);
        }
    }
    
    public void addJavaExtensionPoint(Class<?> clazz) {
    	if(clazz == null) {
    		return;
    	}
    	
    	JavaExtensible extensible = clazz.getAnnotation(JavaExtensible.class);
    	if(extensible != null) {
    		JavaExtensionPoint ep = new JavaExtensionPoint();
    		ep.name = extensible.name();
    		ep.description = extensible.description();
    		ep.clazz = clazz;
    		this.javaExtensionPoints.put(ep.name, ep);
    	}
    }
    
    public List<JavaExtensionPoint> getJavaExtensionPoints() {
    	return new ArrayList<JavaExtensionPoint>(this.javaExtensionPoints.values());
    }
    
    public JavaExtensionPoint getJavaExtensionPoint(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	return this.javaExtensionPoints.get(name);
    }
    
    @SuppressWarnings("rawtypes")
    public String getExtensionNameForClass(Class clazz) {
        if(clazz == null) {
            return null;
        }
        return this.classExtensionNames.get(clazz);
    }
    
    @SuppressWarnings("rawtypes")
    public Object wrapAction(String actionName, Class clazz) {
        if(StringUtils.isBlank(actionName) || clazz == null) {
            return null;
        }
        String extensionName = this.getExtensionNameForClass(clazz);
        if(StringUtils.isBlank(extensionName)) {
            return null;
        }
        try {
            this.tryBindAction(extensionName, actionName);
        }
        catch(Exception e) {
            return null;
        }
        
        Action action = ActionManager.getInstance().getAction(actionName);
        Object impl = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, new ExtInvocationHandler(action));
        return impl;
    }
    
    /**
     * Get an extension impl for the class.
     * The name may be either class name or action name;
     * 
     * @param name
     * @param clazz
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getExtension(String name, Class clazz) {
    	if(StringUtils.isBlank(name) || clazz == null) {
    		return null;
    	}
    	
    	//first try as class name
    	Object obj = ObjectUtils.newInstance(name);
    	if(obj != null && clazz.isAssignableFrom(obj.getClass())) {
    		logger.debug("Loaded extension as class for [" + name + "]");
    		
    		return obj;
    	}
    	
    	//then try as action
    	obj = this.wrapAction(name, clazz);
    	if(obj != null) {
    		logger.debug("Loaded extension as action for [" + name + "]");
    	}
    	else {
    		logger.debug("Failed to load any extension for [" + name + "]");
    	}
    	
    	return obj;
    }
    
    public Action getBoundAction(String extensionName) {
        if(extensionName == null) {
            return null;
        }
        
        String actionName = DataManager.getInstance().getValue(extensionName);
        Action action = ActionManager.getInstance().getAction(actionName);
        if(actionName != null && action == null) {
            logger.info("Failed to find registered action for extension point [" + extensionName + "].");
        }
        return action;
    }
    
    public String getExtensionNameForAction(Action action) {
        if(action == null) {
            return null;
        }
        
        for(String name : this.extensionPoints.keySet()) {
            String actionName = DataManager.getInstance().getValue(name);
            if(actionName != null && actionName.equals(action.name)) {
                return name;
            }
        }
        return null;
    }
    
    public void tryBindAction(String extensionName, String actionName) {
        if(StringUtils.isBlank(extensionName)) {
            return;
        }
        if(StringUtils.isBlank(actionName)) {
            return;
        }
        
        ExtensionPoint ep = this.getExtensionPoint(extensionName);
        if(ep == null) {
            throw new DataException("Cannot find extension point for [" + extensionName + "].");
        }
        
        Action action = ActionManager.getInstance().getAction(actionName);
        if(action == null) {
            throw new DataException("Cannot find action for [" + actionName + "].");
        }
        
        if(ep.paramNames.size() != action.params.size()) {
            throw new DataException("Parameter numbers do not match.");
        }
        
        for(int i = 0; i < ep.paramNames.size(); i++) {
            ActionParam ap = action.params.get(i);
            String param = ep.paramNames.get(i);
            if(!param.equals(ap.name)) {
                throw new DataException("Action parameters do not match.");
            }
        }
    }
    
    public void bindAction(String extensionName, String actionName) {
        if(StringUtils.isBlank(extensionName)) {
            return;
        }
        if(StringUtils.isBlank(actionName)) {
            return;
        }
        
        this.tryBindAction(extensionName, actionName);
        
        DataManager.getInstance().setValue(extensionName, actionName);
    }
    
    public void unbindAction(String extensionName) {
        if(StringUtils.isBlank(extensionName)) {
            return;
        }
        
        DataManager.getInstance().deleteValue(extensionName);
    }
    
    @Override
    public void start() {
        this.loadExtensions();
        
        this.loadJavaExtensions();
    }
    
    private void loadExtensions() {
        logger.info("Load extensions...");
        this.addInterface(Disaster.class);
        this.addInterface(DeathExecution.class);
        this.addInterface(RewardGiver.class);
        this.addInterface(TaskAttrComparator.class);
        this.addInterface(TaskDefaultValueProvider.class);
        this.addInterface(ContextInitializer.class);
        this.addInterface(TaskTemplateEP.class);
        this.addInterface(TaskSpawner.class);
        this.addInterface(QueryHandler.class);
        this.addInterface(TaskFollowerAction.class);
        this.addInterface(StatusActivator.class);
        this.addInterface(StatusDeactivator.class);
        this.addInterface(UserItemEffect.class);
        this.addInterface(SkillCanTrigger.class);
        this.addInterface(SkillTrigger.class);
        this.addInterface(WebJobExecutor.class);
        this.addInterface(QuizProcessor.class);
        this.addInterface(NovelRoleDescriptor.class);
        this.addInterface(NovelRoleValidator.class);
        this.addInterface(NovelRoleReportBuilder.class);
        this.addInterface(NovelRoleImageProvider.class);
    }
    
    private void loadJavaExtensions() {
    	logger.info("Load java extensions...");
    	this.addJavaExtensionPoint(DefaultJob.class);
    	this.addJavaExtensionPoint(ActiveObject.class);
    	this.addJavaExtensionPoint(SystemWebJob.class);
    }

    @Override
    public void shutdown() {
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> clazz) {
        if(clazz == null) {
            return null;
        }
        
        JavaObject javaObject = JavaObjectManager.getInstance().getJavaObjectOfClass(clazz);
        if(javaObject != null && javaObject.object != null) {
        	return (T)javaObject.object;
        }
        
        T ret = (T) this.classExtensions.get(clazz);
        return ret;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        Action action = (Action) event.data.get("data");
        String extensionName = this.getExtensionNameForAction(action);
        if(extensionName != null) {
            this.unbindAction(extensionName);
        }
    }
    
    public boolean isJavaExtension(JavaObject javaObject) {
    	if(javaObject == null || javaObject.object == null) {
    		return false;
    	}
    	
    	for(JavaExtensionPoint ep : this.javaExtensionPoints.values()) {
    		if(ep.clazz.isAssignableFrom(javaObject.object.getClass())) {
    			return true;
    		}
    	}
    	
    	return false;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void created(JavaObject javaObject) {
		if(javaObject != null && javaObject.object != null) {
			if(!this.isJavaExtension(javaObject)) {
				return;
			}
			
			this.javaExtensions.put(javaObject.id, javaObject);
			
			Object obj = javaObject.object;
			for(JavaExtensionListener listener : this.listeners) {
				Class<?> clazz = listener.getExtensionClass();
				if(clazz.isAssignableFrom(obj.getClass())) {
					listener.created(obj);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void removed(JavaObject javaObject) {
		if(javaObject != null && javaObject.object != null) {
			if(!this.isJavaExtension(javaObject)) {
				return;
			}
			
			this.javaExtensions.remove(javaObject.id);
			
			Object obj = javaObject.object;
			for(JavaExtensionListener listener : this.listeners) {
				Class<?> clazz = listener.getExtensionClass();
				if(clazz.isAssignableFrom(obj.getClass())) {
					listener.removed(obj);
				}
			}
		}
	}
	
	public List<JavaObject> getJavaExtensions() {
		return new ArrayList<JavaObject>(this.javaExtensions.values());
	}
	
	public JavaObject getJavaExtension(int id) {
		return this.javaExtensions.get(id);
	}
	
	public JavaObject getJavaExtension(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(JavaObject javaObject : this.javaExtensions.values()) {
			if(name.equals(javaObject.name)) {
				return javaObject;
			}
		}
		
		return null;
	}
	
	public void addJavaExtensionListener(JavaExtensionListener<?> listener) {
		if(listener != null) {
			this.listeners.add(listener);
		}
	}
	
	public void removeJavaExtensionListener(JavaExtensionListener<?> listener) {
		if(listener != null) {
			this.listeners.remove(listener);
		}
	}
}

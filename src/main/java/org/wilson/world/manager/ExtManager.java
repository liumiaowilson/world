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
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Action;
import org.wilson.world.model.ActionParam;
import org.wilson.world.model.ExtensionPoint;
import org.wilson.world.novel.NovelRoleDescriptor;
import org.wilson.world.query.QueryHandler;
import org.wilson.world.quiz.QuizProcessor;
import org.wilson.world.reward.RewardGiver;
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
import org.wilson.world.web.WebJobExecutor;

public class ExtManager implements ManagerLifecycle, EventListener {
    private static final Logger logger = Logger.getLogger(ExtManager.class);
    
    private static ExtManager instance;
    
    private Map<String, Object> extensions = new HashMap<String, Object>();
    @SuppressWarnings("rawtypes")
    private Map<Class, Object> classExtensions = new HashMap<Class, Object>();
    @SuppressWarnings("rawtypes")
    private Map<Class, String> classExtensionNames = new HashMap<Class, String>();
    private Map<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();
    
    private ExtManager() {
        EventManager.getInstance().registerListener(EventType.DeleteAction, this);
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
        }
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
    }

    @Override
    public void shutdown() {
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> clazz) {
        if(clazz == null) {
            return null;
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
}

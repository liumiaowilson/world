package org.wilson.world.ext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ActionManager;
import org.wilson.world.model.Action;

public class ExtInvocationHandler implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(ExtInvocationHandler.class);
    private Action action;
    
    public ExtInvocationHandler(Action action) {
        this.action = action;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Scriptable scriptable = method.getAnnotation(Scriptable.class);
        if(scriptable == null) {
            logger.error("failed to find scriptable annotation!");
            return null;
        }
        String [] params = scriptable.params();
        if(params.length != args.length) {
            logger.error("params and args do not match!");
            return null;
        }
        Map<String, Object> context = new HashMap<String, Object>();
        for(int i = 0; i < params.length; i++) {
            context.put(params[i], args[i]);
        }
        
        Object ret = ActionManager.getInstance().run(action, context);
        
        if(method.getReturnType() == int.class) {
            if(ret instanceof Double) {
                return ((Double)ret).intValue();
            }
        }
        
        return ret;
    }

}

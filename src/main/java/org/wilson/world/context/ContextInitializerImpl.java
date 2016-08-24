package org.wilson.world.context;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.util.ValueHolder;

public class ContextInitializerImpl implements ContextInitializer {

    @Override
    public void setCurrentContext() {
        HttpServletRequest request = ValueHolder.getRequest();
        if(request == null) {
            return;
        }
        
        if(request.getSession() == null) {
            return;
        }
        
        TimeZone tz = (TimeZone) request.getSession().getAttribute("world-timezone");
        if(tz == null) {
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        cal.setTime(new Date());
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            this.setContextAsLeisure();
        }
        else {
            if(hour <= 10 || hour >= 18) {
                this.setContextAsLeisure();
            }
            else {
                this.setContextAsWork();
            }
        }
    }
    
    private void setContextAsWork() {
        ContextManager.getInstance().setCurrentContext(ContextManager.CONTEXT_WORK);
    }

    private void setContextAsLeisure() {
        ContextManager.getInstance().setCurrentContext(ContextManager.CONTEXT_LEISURE);
    }
}

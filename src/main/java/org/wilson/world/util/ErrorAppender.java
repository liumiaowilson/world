package org.wilson.world.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.wilson.world.manager.ErrorInfoManager;
import org.wilson.world.model.ErrorInfo;

public class ErrorAppender extends AppenderSkeleton {

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void append(LoggingEvent event) {
        if(event == null) {
            return;
        }
        if(event.level.isGreaterOrEqual(Priority.ERROR)) {
            ThrowableInformation ti = event.getThrowableInformation();
            if(ti != null) {
                ErrorInfo info = new ErrorInfo();
                String name = "Unknown exception";
                Throwable t = ti.getThrowable();
                if(t != null) {
                    name = t.getClass().getCanonicalName();
                }
                info.name = name;
                info.trace = ti.getThrowableStrRep();
                ErrorInfoManager.getInstance().createErrorInfo(info);
            }
        }
    }

}

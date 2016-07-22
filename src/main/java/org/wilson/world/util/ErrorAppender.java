package org.wilson.world.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.wilson.world.manager.ConsoleManager;

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
            ConsoleManager.getInstance().addError(event.getThrowableStrRep());
        }
    }

}

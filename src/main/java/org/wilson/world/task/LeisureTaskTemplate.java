package org.wilson.world.task;

import org.wilson.world.manager.ContextManager;

public class LeisureTaskTemplate extends DefaultTaskTemplate {
    @Override
    public String getName() {
        return ContextManager.CONTEXT_LEISURE;
    }

}

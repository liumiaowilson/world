package org.wilson.world.query;

import java.util.List;
import java.util.Map;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.QueryItem;

public interface QueryHandler {
    public static final String EXTENSION_POINT = "query.handler";
    
    @Scriptable(name = EXTENSION_POINT, description = "Execute a query. Assign the value of this action to query instance.", params = { "args" })
    public List<QueryItem> doQuery(Map<String, String> args);
}

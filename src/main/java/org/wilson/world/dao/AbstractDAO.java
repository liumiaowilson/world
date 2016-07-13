package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAO<T> implements DAO<T> {
    private Map<String, QueryTemplate<T>> templates = new HashMap<String, QueryTemplate<T>>();

    public void addQueryTemplate(QueryTemplate<T> template) {
        if(template != null) {
            this.templates.put(template.getID(), template);
        }
    }
    
    @Override
    public List<String> getQueryTemplateNames() {
        return new ArrayList<String>(templates.keySet());
    }

    @Override
    public QueryTemplate<T> getQueryTemplate(String name) {
        return this.templates.get(name);
    }

    @Override
    public void init() {
    }
}

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

    @Override
    public StringBuffer export() {
        StringBuffer sb = new StringBuffer();
        for(T t : this.getAll(false)) {
            sb.append(this.exportSingle(t));
            sb.append("\n");
        }
        return sb;
    }
    
    
    @Override
    public boolean isLazy() {
        return false;
    }

    @Override
    public T get(int id) {
        return this.get(id, this.isLazy());
    }

    @Override
    public List<T> getAll() {
        return this.getAll(this.isLazy());
    }

    @Override
    public boolean isLoaded(T t) {
        return true;
    }

    @Override
    public T load(T t) {
        int id = this.getId(t);
        return this.get(id, false);
    }

    @Override
    public T unload(T t) {
        return t;
    }

    protected String escapeStr(String str) {
        if(str == null) {
            return null;
        }
        
        str = str.replaceAll("'", "\\\\'");
        return "'" + str + "'";
    }
    
    public abstract StringBuffer exportSingle(T t);
}

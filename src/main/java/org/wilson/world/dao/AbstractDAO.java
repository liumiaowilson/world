package org.wilson.world.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
        for(T t : this.getAll()) {
            sb.append(this.exportSingle(t));
            sb.append("\n");
        }
        return sb;
    }
    
    protected String escape(String str) {
        if(StringUtils.isBlank(str)) {
            return str;
        }
        str = str.replaceAll("'", "\\\\'");
        return str;
    }
    
    public abstract StringBuffer exportSingle(T t);
}

package org.wilson.world.dao;

import java.util.List;

public interface DAO<T> {
    public void create(T t);
    
    public void update(T t);
    
    public void delete(int id);
    
    public T get(int id);
    
    public List<T> getAll();
    
    public List<T> query(QueryTemplate template, Object ... args);
    
    public List<String> getQueryTemplateNames();
    
    public QueryTemplate getQueryTemplate(String name);
    
    public void init();
    
    public String getItemTableName();
}

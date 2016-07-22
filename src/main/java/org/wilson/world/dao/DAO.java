package org.wilson.world.dao;

import java.util.List;

public interface DAO<T> {
    public void create(T t);
    
    public void update(T t);
    
    public void delete(int id);
    
    public T get(int id);
    
    public int getId(T t);
    
    public List<T> getAll();
    
    public List<T> query(QueryTemplate<T> template, Object ... args);
    
    public List<String> getQueryTemplateNames();
    
    public QueryTemplate<T> getQueryTemplate(String name);
    
    public void init();
    
    public String getItemTableName();
    
    public StringBuffer export();
}

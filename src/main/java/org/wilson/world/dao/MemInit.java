package org.wilson.world.dao;

import java.util.List;

public interface MemInit<T> {
    public void init(DAO<T> dao);
    
    public List<QueryTemplate<T>> getQueryTemplates();
}

package org.wilson.world.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.exception.DataException;

public class MemoryDAO<T> extends AbstractDAO<T> {
    private static final Logger logger = Logger.getLogger(MemoryDAO.class);
    private Map<Integer, T> data = new HashMap<Integer, T>();
    private String name;
    private int global_id = 5;
    
    public MemoryDAO(String name) {
        this.name = name;
    }
    
    @Override
    public void create(T t) {
        int id = global_id++;
        this.setId(t, id);
        data.put(id, t);
    }

    @Override
    public void update(T t) {
        int id = this.getId(t);
        data.put(id, t);
    }

    @Override
    public void delete(int id) {
        data.remove(id);
    }

    @Override
    public T get(int id) {
        return data.get(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int getId(T t) {
        if(t == null) {
            return 0;
        }
        try {
            Class clazz = t.getClass();
            Field f = clazz.getDeclaredField("id");
            int id = (Integer)f.get(t);
            return id;
        }
        catch(Exception e) {
            logger.error(e);
            throw new DataException(e.getMessage());
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void setId(T t, int id) {
        if(t == null) {
            return;
        }
        try {
            Class clazz = t.getClass();
            Field f = clazz.getDeclaredField("id");
            f.set(t, id);
        }
        catch(Exception e) {
            logger.error(e);
            throw new DataException(e.getMessage());
        }
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<T>(data.values());
    }

    @Override
    public List<T> query(QueryTemplate<T> template, Object... args) {
        List<T> ret = new ArrayList<T>();
        for(T t : this.getAll()) {
            if(template.accept(t, args)) {
                ret.add(t);
            }
        }
        return ret;
    }

    @Override
    public String getItemTableName() {
        return this.name;
    }
}

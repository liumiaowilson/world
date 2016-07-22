package org.wilson.world.cache;

import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.dao.QueryTemplate;

public class CachedDAO<V> implements DAO<V> {
    private Cache<Integer, V> cache;
    private DAO<V> dao;
    
    public CachedDAO(Cache<Integer, V> cache, DAO<V> dao) {
        this.cache = cache;
        this.dao = dao;
    }
    
    public Cache<Integer, V> getCache() {
        return this.cache;
    }
    
    @Override
    public void create(V t) {
        this.dao.create(t);
        int id = this.dao.getId(t);
        this.cache.put(id, t);
    }

    @Override
    public void update(V t) {
        this.dao.update(t);
        int id = this.dao.getId(t);
        this.cache.put(id, t);
    }

    @Override
    public void delete(int id) {
        this.dao.delete(id);
        this.cache.delete(id);
    }

    @Override
    public V get(int id) {
        V ret = this.cache.get(id);
        if(ret != null) {
            return ret;
        }
        else {
            ret = this.dao.get(id);
            if(ret == null) {
                return null;
            }
            else {
                this.cache.put(id, ret);
                return ret;
            }
        }
    }

    @Override
    public int getId(V t) {
        return this.dao.getId(t);
    }

    @Override
    public List<V> getAll() {
        return this.cache.getAll();
    }

    @Override
    public List<V> query(QueryTemplate<V> template, Object... args) {
        List<V> ret = this.dao.query(template, args);
        
        for(V v : ret) {
            int id = this.dao.getId(v);
            this.cache.put(id, v);
        }
        
        return ret;
    }

    @Override
    public List<String> getQueryTemplateNames() {
        return this.dao.getQueryTemplateNames();
    }

    @Override
    public QueryTemplate<V> getQueryTemplate(String name) {
        return this.dao.getQueryTemplate(name);
    }

    @Override
    public void init() {
        this.dao.init();
        
        this.cache.init(new CacheLoader<Integer, V>(){
            @Override
            public void load(Cache<Integer, V> cache) {
                List<V> all = CachedDAO.this.dao.getAll();
                for(V v : all) {
                    int id = CachedDAO.this.dao.getId(v);
                    cache.put(id, v);
                }
            }
        });
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public StringBuffer export() {
        return this.dao.export();
    }

}

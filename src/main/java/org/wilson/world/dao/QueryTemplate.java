package org.wilson.world.dao;

public interface QueryTemplate<T> {
    public String getID();
    
    public String getSQL();
    
    public QueryHelper getQueryHelper();
    
    /**
     * Used in memory mode
     * @param t
     * @return
     */
    public boolean accept(T t, Object... args);
}

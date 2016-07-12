package org.wilson.world.dao;

public interface QueryTemplate {
    public String getID();
    
    public String getSQL();
    
    public QueryHelper getQueryHelper();
}

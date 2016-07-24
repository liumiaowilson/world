package org.wilson.world.query;

import java.util.List;

import org.wilson.world.model.QueryItem;

public interface QueryProcessor {
    public int getID();
    
    public String getName();
    
    public List<QueryItem> query();
    
    public String getIDCellExpression();
    
    public String getNameCellExpression();
    
    public List<QueryButtonConfig> getQueryButtonConfigs();
    
    public boolean isQuickLink();
}

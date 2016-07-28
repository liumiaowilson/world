package org.wilson.world.query;

import java.util.List;
import java.util.Map;

import org.wilson.world.model.QueryItem;

public interface QueryProcessor {
    public int getID();
    
    public String getName();
    
    public List<QueryItem> query(Map<String, String> args);
    
    public String getIDCellExpression();
    
    public String getNameCellExpression();
    
    public List<QueryButtonConfig> getQueryButtonConfigs();
    
    public boolean isQuickLink();
}

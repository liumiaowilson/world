package org.wilson.world.item;

import org.wilson.world.dao.DAO;

public interface ItemTypeProvider {
    public String getItemTypeName();
    
    public String getID(Object target);
    
    /**
     * Used to identify the unique item
     * 
     * @param target
     * @return
     */
    public String getIdentifier(Object target);
    
    public boolean accept(Object target);
    
    public String getItemTableName();
    
    @SuppressWarnings("rawtypes")
    public DAO getDAO();
}

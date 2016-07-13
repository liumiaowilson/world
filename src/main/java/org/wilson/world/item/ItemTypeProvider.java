package org.wilson.world.item;

public interface ItemTypeProvider {
    public String getItemTypeName();
    
    public String getID(Object target);
    
    public boolean accept(Object target);
    
    public String getItemTableName();
    
    public int getItemCount();
}

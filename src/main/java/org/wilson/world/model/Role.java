package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class Role {
    public int id;
    
    public String name;
    
    public String description;
    
    public String attrIds;
    
    public List<RoleAttr> attrs = new ArrayList<RoleAttr>();
    
    public boolean hasRoleAttr(int attrId) {
        for(RoleAttr attr : attrs) {
            if(attr.id == attrId) {
                return true;
            }
        }
        
        return false;
    }
}

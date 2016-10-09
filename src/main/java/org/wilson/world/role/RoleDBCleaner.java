package org.wilson.world.role;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.item.DBCleaner;
import org.wilson.world.manager.RoleAttrManager;
import org.wilson.world.manager.RoleManager;
import org.wilson.world.model.Role;
import org.wilson.world.model.RoleAttr;

public class RoleDBCleaner implements DBCleaner {

    @Override
    public void clean() {
        List<Role> roles = RoleManager.getInstance().getRoles();
        for(Role role : roles) {
            List<RoleAttr> attrs = new ArrayList<RoleAttr>();
            for(RoleAttr attr : role.attrs) {
                RoleAttr attrDef = RoleAttrManager.getInstance().getRoleAttr(attr.id);
                if(attrDef != null) {
                    attrs.add(attrDef);
                }
            }
            
            if(attrs.size() != role.attrs.size()) {
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < attrs.size(); i++) {
                    RoleAttr attr = attrs.get(i);
                    sb.append(attr.id);
                    if(i != attrs.size() - 1) {
                        sb.append(",");
                    }
                }
                
                role.attrIds = sb.toString();
                RoleManager.getInstance().updateRole(role);
            }
        }
    }

}

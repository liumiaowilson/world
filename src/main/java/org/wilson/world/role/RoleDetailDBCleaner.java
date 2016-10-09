package org.wilson.world.role;

import java.util.List;

import org.wilson.world.item.DBCleaner;
import org.wilson.world.manager.RoleAttrManager;
import org.wilson.world.manager.RoleDetailManager;
import org.wilson.world.manager.RoleManager;
import org.wilson.world.model.Role;
import org.wilson.world.model.RoleAttr;
import org.wilson.world.model.RoleDetail;

public class RoleDetailDBCleaner implements DBCleaner {

    @Override
    public void clean() {
        List<RoleDetail> details = RoleDetailManager.getInstance().getRoleDetails();
        for(RoleDetail detail : details) {
            boolean shouldDelete = false;
            
            Role role = RoleManager.getInstance().getRole(detail.roleId);
            if(role == null) {
                shouldDelete = true;
            }
            
            if(!shouldDelete) {
                RoleAttr attr = RoleAttrManager.getInstance().getRoleAttr(detail.roleAttrId);
                if(attr == null) {
                    shouldDelete = true;
                }
            }
            
            if(shouldDelete) {
                RoleDetailManager.getInstance().deleteRoleDetail(detail.id);
            }
        }
    }

}

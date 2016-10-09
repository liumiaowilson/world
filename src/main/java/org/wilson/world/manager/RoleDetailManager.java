package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Role;
import org.wilson.world.model.RoleAttr;
import org.wilson.world.model.RoleDetail;
import org.wilson.world.role.RoleDetailDBCleaner;

public class RoleDetailManager implements ItemTypeProvider {
    public static final String NAME = "role_detail";
    
    private static RoleDetailManager instance;
    
    private DAO<RoleDetail> dao = null;
    
    @SuppressWarnings("unchecked")
    private RoleDetailManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(RoleDetail.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new RoleDetailDBCleaner());
    }
    
    public static RoleDetailManager getInstance() {
        if(instance == null) {
            instance = new RoleDetailManager();
        }
        return instance;
    }
    
    public void createRoleDetail(RoleDetail detail) {
        ItemManager.getInstance().checkDuplicate(detail);
        
        this.dao.create(detail);
    }
    
    private RoleDetail loadRoleDetail(RoleDetail detail) {
        Role role = RoleManager.getInstance().getRole(detail.roleId);
        if(role != null) {
            RoleAttr attr = RoleAttrManager.getInstance().getRoleAttr(detail.roleAttrId);
            if(attr != null) {
                detail.name = role.name + " - " + attr.name;
            }
        }
        return detail;
    }
    
    public RoleDetail getRoleDetail(int id) {
        RoleDetail detail = this.dao.get(id);
        if(detail != null) {
            detail = this.loadRoleDetail(detail);
            return detail;
        }
        else {
            return null;
        }
    }
    
    public List<RoleDetail> getRoleDetails() {
        List<RoleDetail> result = new ArrayList<RoleDetail>();
        for(RoleDetail detail : this.dao.getAll()) {
            detail = this.loadRoleDetail(detail);
            result.add(detail);
        }
        return result;
    }
    
    public void updateRoleDetail(RoleDetail detail) {
        this.dao.update(detail);
    }
    
    public void deleteRoleDetail(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof RoleDetail;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        RoleDetail detail = (RoleDetail)target;
        return String.valueOf(detail.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return this.getID(target);
    }
    
    public List<RoleDetail> getRoleDetails(int roleId) {
        List<RoleDetail> ret = new ArrayList<RoleDetail>();
        
        for(RoleDetail detail : this.getRoleDetails()) {
            if(detail.roleId == roleId) {
                ret.add(detail);
            }
        }
        
        return ret;
    }
    
    public RoleDetail getRoleDetail(int roleId, int roleAttrId) {
        List<RoleDetail> details = this.getRoleDetails(roleId);
        for(RoleDetail detail : details) {
            if(detail.roleAttrId == roleAttrId) {
                return detail;
            }
        }
        
        return null;
    }
}

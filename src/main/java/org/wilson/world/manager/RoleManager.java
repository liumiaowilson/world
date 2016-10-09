package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Role;
import org.wilson.world.model.RoleAttr;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RoleManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(RoleManager.class);
    
    public static final String NAME = "role";
    
    private static RoleManager instance;
    
    private DAO<Role> dao = null;
    
    @SuppressWarnings("unchecked")
    private RoleManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Role.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Role role : getRoles()) {
                    boolean found = role.name.contains(text) || role.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = role.id;
                        content.name = role.name;
                        content.description = role.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RoleManager getInstance() {
        if(instance == null) {
            instance = new RoleManager();
        }
        return instance;
    }
    
    public void createRole(Role role) {
        ItemManager.getInstance().checkDuplicate(role);
        
        this.dao.create(role);
    }
    
    private Role loadRole(Role role) {
        for(String attrId : role.attrIds.trim().split(",")) {
            if(!StringUtils.isBlank(attrId)) {
                try {
                    RoleAttr attr = RoleAttrManager.getInstance().getRoleAttr(Integer.parseInt(attrId));
                    role.attrs.add(attr);
                }
                catch(Exception e) {
                    logger.error(e);
                }
            }
        }
        
        return role;
    }
    
    public Role getRole(int id) {
        Role role = this.dao.get(id);
        if(role != null) {
            role = this.loadRole(role);
            return role;
        }
        else {
            return null;
        }
    }
    
    public List<Role> getRoles() {
        List<Role> result = new ArrayList<Role>();
        for(Role role : this.dao.getAll()) {
            role = this.loadRole(role);
            result.add(role);
        }
        return result;
    }
    
    public void updateRole(Role role) {
        this.dao.update(role);
    }
    
    public void deleteRole(int id) {
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
        return target instanceof Role;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Role role = (Role)target;
        return String.valueOf(role.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Role role = (Role)target;
        return role.name;
    }
}

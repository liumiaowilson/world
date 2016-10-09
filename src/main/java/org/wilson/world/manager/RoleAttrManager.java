package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.RoleAttr;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class RoleAttrManager implements ItemTypeProvider {
    public static final String NAME = "role_attr";
    
    private static RoleAttrManager instance;
    
    private DAO<RoleAttr> dao = null;
    
    @SuppressWarnings("unchecked")
    private RoleAttrManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(RoleAttr.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(RoleAttr attr : getRoleAttrs()) {
                    boolean found = attr.name.contains(text) || attr.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = attr.id;
                        content.name = attr.name;
                        content.description = attr.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static RoleAttrManager getInstance() {
        if(instance == null) {
            instance = new RoleAttrManager();
        }
        return instance;
    }
    
    public void createRoleAttr(RoleAttr attr) {
        ItemManager.getInstance().checkDuplicate(attr);
        
        this.dao.create(attr);
    }
    
    public RoleAttr getRoleAttr(int id) {
        RoleAttr attr = this.dao.get(id);
        if(attr != null) {
            return attr;
        }
        else {
            return null;
        }
    }
    
    public List<RoleAttr> getRoleAttrs() {
        List<RoleAttr> result = new ArrayList<RoleAttr>();
        for(RoleAttr attr : this.dao.getAll()) {
            result.add(attr);
        }
        return result;
    }
    
    public void updateRoleAttr(RoleAttr attr) {
        this.dao.update(attr);
    }
    
    public void deleteRoleAttr(int id) {
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
        return target instanceof RoleAttr;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        RoleAttr attr = (RoleAttr)target;
        return String.valueOf(attr.id);
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
        
        RoleAttr attr = (RoleAttr)target;
        return attr.name;
    }
}

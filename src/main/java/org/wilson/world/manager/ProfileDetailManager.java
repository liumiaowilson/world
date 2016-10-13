package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ProfileDetail;
import org.wilson.world.profile.ProfileDetailType;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ProfileDetailManager implements ItemTypeProvider {
    public static final String NAME = "profile_detail";
    
    private static ProfileDetailManager instance;
    
    private DAO<ProfileDetail> dao = null;
    
    private List<String> types = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private ProfileDetailManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ProfileDetail.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ProfileDetail detail : getProfileDetails()) {
                    boolean found = detail.name.contains(text) || detail.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = detail.id;
                        content.name = detail.name;
                        content.description = detail.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.initProfileDetailTypes();
    }
    
    private void initProfileDetailTypes() {
        for(ProfileDetailType type : ProfileDetailType.values()) {
            this.types.add(type.name());
        }
    }
    
    public static ProfileDetailManager getInstance() {
        if(instance == null) {
            instance = new ProfileDetailManager();
        }
        return instance;
    }
    
    public void createProfileDetail(ProfileDetail detail) {
        ItemManager.getInstance().checkDuplicate(detail);
        
        this.dao.create(detail);
    }
    
    public ProfileDetail getProfileDetail(int id) {
        ProfileDetail detail = this.dao.get(id);
        if(detail != null) {
            return detail;
        }
        else {
            return null;
        }
    }
    
    public List<ProfileDetail> getProfileDetails() {
        List<ProfileDetail> result = new ArrayList<ProfileDetail>();
        for(ProfileDetail detail : this.dao.getAll()) {
            result.add(detail);
        }
        return result;
    }
    
    public void updateProfileDetail(ProfileDetail detail) {
        this.dao.update(detail);
    }
    
    public void deleteProfileDetail(int id) {
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
        return target instanceof ProfileDetail;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ProfileDetail detail = (ProfileDetail)target;
        return String.valueOf(detail.id);
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
        
        ProfileDetail detail = (ProfileDetail)target;
        return detail.name;
    }
    
    public List<String> getProfileDetailTypes() {
        return this.types;
    }
    
    public List<ProfileDetail> getProfileDetailsOfType(String type) {
        if(StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        
        List<ProfileDetail> ret = new ArrayList<ProfileDetail>();
        
        for(ProfileDetail detail : this.getProfileDetails()) {
            if(type.equals(detail.type)) {
                ret.add(detail);
            }
        }
        
        return ret;
    }
}

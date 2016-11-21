package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.model.NovelStage;
import org.wilson.world.model.NovelVariable;
import org.wilson.world.novel.NovelRoleDescriptor;
import org.wilson.world.novel.NovelRoleInfo;
import org.wilson.world.novel.NovelRoleReport;
import org.wilson.world.novel.NovelRoleReportBuilder;
import org.wilson.world.novel.NovelRoleValidator;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

import net.sf.json.JSONObject;

public class NovelRoleManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(NovelRoleManager.class);
	
    public static final String NAME = "novel_role";
    
    private static NovelRoleManager instance;
    
    private DAO<NovelRole> dao = null;
    
    @SuppressWarnings("unchecked")
    private NovelRoleManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelRole.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelRole role : getNovelRoles()) {
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
    
    public static NovelRoleManager getInstance() {
        if(instance == null) {
            instance = new NovelRoleManager();
        }
        return instance;
    }
    
    public void createNovelRole(NovelRole role) {
        ItemManager.getInstance().checkDuplicate(role);
        
        this.dao.create(role);
    }
    
    public NovelRole getNovelRole(int id) {
    	NovelRole role = this.dao.get(id);
        if(role != null) {
        	role = this.loadNovelRole(role);
            return role;
        }
        else {
            return null;
        }
    }
    
    private NovelRole loadNovelRole(NovelRole role) {
    	try {
    		JSONObject obj = JSONObject.fromObject(role.definition);
    		for(Object keyObj : obj.keySet()) {
    			String key = (String)keyObj;
    			String value = obj.getString(key);
    			role.variables.put(key, value);
    		}
    		
    		for(NovelVariable var : NovelVariableManager.getInstance().getNovelVariables()) {
    			if(!role.variables.containsKey(var.name)) {
    				role.variables.put(var.name, var.defaultValue);
    			}
    		}
    		
    		NovelRoleDescriptor descriptor = ExtManager.getInstance().getExtension(NovelRoleDescriptor.class);
    		if(descriptor != null) {
    			role.display = descriptor.getDescription(role);
    		}
    	}
    	catch(Exception e) {
    		logger.error(e);
    	}
    	return role;
    }
    
    public List<NovelRole> getNovelRoles() {
        List<NovelRole> result = new ArrayList<NovelRole>();
        for(NovelRole role : this.dao.getAll()) {
        	role = this.loadNovelRole(role);
            result.add(role);
        }
        return result;
    }
    
    public List<NovelRole> getNovelRoles(String script) {
    	if(StringUtils.isBlank(script)) {
    		return Collections.emptyList();
    	}
    	
    	List<NovelRole> roles = new ArrayList<NovelRole>();
    	
    	for(NovelRole role : this.getNovelRoles()) {
    		try {
    			Object ret = NovelFragmentManager.getInstance().runScript(script, role);
    			if(ret instanceof Boolean) {
    				if((Boolean)ret) {
    					roles.add(role);
    				}
    			}
    		}
    		catch(Exception e) {
    		}
    	}
    	
    	return roles;
    }
    
    public void updateNovelRole(NovelRole role) {
        this.dao.update(role);
    }
    
    public void deleteNovelRole(int id) {
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
        return target instanceof NovelRole;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelRole role = (NovelRole)target;
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
        
        NovelRole role = (NovelRole)target;
        return role.name;
    }
    
    public NovelRole randomNovelRole() {
    	List<NovelRole> roles = this.getNovelRoles();
    	if(roles.isEmpty()) {
    		return null;
    	}
    	
    	int n = DiceManager.getInstance().random(roles.size());
    	return roles.get(n);
    }

    public List<NovelFragment> getStartNovelFragmentsFor(NovelRole role) {
    	if(role == null) {
    		return Collections.emptyList();
    	}
    	
    	NovelStage stage = NovelStageManager.getInstance().getStartStage();
    	if(stage == null) {
    		return Collections.emptyList();
    	}
    	
    	List<NovelFragment> fragments = NovelFragmentManager.getInstance().getNovelFragmentsOfStage(stage.id);
    	List<NovelFragment> ret = new ArrayList<NovelFragment>();
    	for(NovelFragment fragment : fragments) {
    		if(fragment.isAvailableFor(role)) {
    			ret.add(fragment);
    		}
    	}
    	
    	return ret;
    }
    
    public List<NovelRoleInfo> validateAll() {
    	List<NovelRoleInfo> infos = new ArrayList<NovelRoleInfo>();
    	
    	NovelRoleValidator validator = ExtManager.getInstance().getExtension(NovelRoleValidator.class);
    	if(validator != null) {
    		for(NovelRole role : this.getNovelRoles()) {
    			String msg = validator.validate(role);
    			if(msg != null) {
    				NovelRoleInfo info = new NovelRoleInfo();
    				info.id = role.id;
    				info.name = role.name;
    				info.message = msg;
    				infos.add(info);
    			}
    		}
    	}
    	
    	return infos;
    }
    
    public List<NovelRoleReport> buildReports() {
    	NovelRoleReportBuilder builder = ExtManager.getInstance().getExtension(NovelRoleReportBuilder.class);
    	if(builder == null) {
    		return Collections.emptyList();
    	}
    	
    	return builder.build();
    }
}

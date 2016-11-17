package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.model.NovelRole;
import org.wilson.world.novel.DefaultNovelFragmentValidator;
import org.wilson.world.novel.NovelFragmentInfo;
import org.wilson.world.novel.NovelFragmentValidator;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelFragmentManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(NovelFragmentManager.class);
	
    public static final String NAME = "novel_fragment";
    
    private static NovelFragmentManager instance;
    
    private DAO<NovelFragment> dao = null;
    
    private List<NovelFragmentValidator> validators = new ArrayList<NovelFragmentValidator>();
    
    @SuppressWarnings("unchecked")
    private NovelFragmentManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(NovelFragment.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(NovelFragment fragment : getNovelFragments()) {
                    boolean found = fragment.name.contains(text) || fragment.content.contains(text) || fragment.condition.contains(text)
                    		|| fragment.preCode.contains(text) || fragment.postCode.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = fragment.id;
                        content.name = fragment.name;
                        content.description = fragment.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.addValidator(new DefaultNovelFragmentValidator());
    }
    
    public static NovelFragmentManager getInstance() {
        if(instance == null) {
            instance = new NovelFragmentManager();
        }
        return instance;
    }
    
    public void addValidator(NovelFragmentValidator validator) {
    	if(validator != null) {
    		this.validators.add(validator);
    	}
    }
    
    public void removeValidator(NovelFragmentValidator validator) {
    	if(validator != null) {
    		this.validators.remove(validator);
    	}
    }
    
    public void createNovelFragment(NovelFragment fragment) {
        ItemManager.getInstance().checkDuplicate(fragment);
        
        this.dao.create(fragment);
    }
    
    public NovelFragment getNovelFragment(int id) {
    	NovelFragment fragment = this.dao.get(id);
        if(fragment != null) {
            return fragment;
        }
        else {
            return null;
        }
    }
    
    public List<NovelFragment> getNovelFragments() {
        List<NovelFragment> result = new ArrayList<NovelFragment>();
        for(NovelFragment fragment : this.dao.getAll()) {
            result.add(fragment);
        }
        return result;
    }
    
    public List<NovelFragmentInfo> validateAll() {
    	List<NovelFragmentInfo> infos = new ArrayList<NovelFragmentInfo>();
    	
    	for(NovelFragment fragment : this.getNovelFragments()) {
    		String msg = this.validate(fragment);
    		if(msg != null) {
    			NovelFragmentInfo info = new NovelFragmentInfo();
    			info.id = fragment.id;
    			info.name = fragment.name;
    			info.message = msg;
    			infos.add(info);
    		}
    	}
    	
    	return infos;
    }
    
    public void updateNovelFragment(NovelFragment fragment) {
        this.dao.update(fragment);
    }
    
    public void deleteNovelFragment(int id) {
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
        return target instanceof NovelFragment;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        NovelFragment fragment = (NovelFragment)target;
        return String.valueOf(fragment.id);
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
        
        NovelFragment fragment = (NovelFragment)target;
        return fragment.name;
    }
    
    public List<NovelFragment> getNovelFragmentsOfStage(int stageId) {
    	List<NovelFragment> fragments = new ArrayList<NovelFragment>();
    	
    	for(NovelFragment fragment : this.getNovelFragments()) {
    		if(fragment.stageId == stageId) {
    			fragments.add(fragment);
    		}
    	}
    	
    	return fragments;
    }
    
    private Object runScript(String script, NovelRole role) {
    	Map<String, Object> context = new HashMap<String, Object>();
		for(Entry<String, String> entry : role.variables.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		
		context.put("vars", NovelVariableManager.getInstance());
		context.putAll(NovelVariableManager.getInstance().getRuntimeVars());
		
		try {
			Object ret = ScriptManager.getInstance().run(script, context);
			return ret;
		}
		catch(Exception e) {
			logger.error(e);
			return null;
		}
    }
    
    public boolean isAvailableFor(NovelFragment fragment, NovelRole role) {
    	if(fragment == null || role == null) {
    		return false;
    	}
    	
    	if(StringUtils.isNotBlank(fragment.condition)) {
    		Object ret = this.runScript(fragment.condition, role);
    		if(ret instanceof Boolean) {
    			return (Boolean)ret;
    		}
    		else {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public Object runPreCode(NovelFragment fragment, NovelRole role) {
    	if(fragment == null || role == null) {
    		return null;
    	}
    	
    	if(StringUtils.isNotBlank(fragment.preCode)) {
    		return this.runScript(fragment.preCode, role);
    	}
    	
    	return null;
    }
    
    public Object runPostCode(NovelFragment fragment, NovelRole role) {
    	if(fragment == null || role == null) {
    		return null;
    	}
    	
    	if(StringUtils.isNotBlank(fragment.postCode)) {
    		return this.runScript(fragment.postCode, role);
    	}
    	
    	return null;
    }
    
    public String toString(NovelFragment fragment, NovelRole role) {
    	if(fragment == null || role == null) {
    		return null;
    	}
    	
    	String content = fragment.content;
    	for(Entry<String, String> entry : role.variables.entrySet()) {
    		String key = entry.getKey();
    		String value = entry.getValue();
    		content = content.replaceAll("\\{" + key + "\\}", value);
    	}
    	
    	return content;
    }
    
    public String validate(NovelFragment fragment) {
    	if(fragment == null) {
    		return null;
    	}
    	
    	for(NovelFragmentValidator validator : this.validators) {
    		String msg = validator.validate(fragment);
    		if(msg != null) {
    			return msg;
    		}
    	}
    	
    	return null;
    }
}

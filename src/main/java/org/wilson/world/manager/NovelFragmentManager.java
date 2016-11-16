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
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelFragmentManager implements ItemTypeProvider {
	private static final Logger logger = Logger.getLogger(NovelFragmentManager.class);
	
    public static final String NAME = "novel_fragment";
    
    private static NovelFragmentManager instance;
    
    private DAO<NovelFragment> dao = null;
    
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
                    boolean found = fragment.name.contains(text) || fragment.content.contains(text);
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
    }
    
    public static NovelFragmentManager getInstance() {
        if(instance == null) {
            instance = new NovelFragmentManager();
        }
        return instance;
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
    
    public boolean isAvailableFor(NovelFragment fragment, NovelRole role) {
    	if(fragment == null || role == null) {
    		return false;
    	}
    	
    	if(StringUtils.isNotBlank(fragment.condition)) {
    		Map<String, Object> context = new HashMap<String, Object>();
    		for(Entry<String, String> entry : role.variables.entrySet()) {
    			context.put(entry.getKey(), entry.getValue());
    		}
    		
    		try {
    			Object ret = ScriptManager.getInstance().run(fragment.condition, context);
    			if(ret instanceof Boolean) {
    				return (Boolean)ret;
    			}
    			else {
    				logger.error("Abnormal return code for fragment [" + fragment.name + "]: " + ret);
    				return false;
    			}
    		}
    		catch(Exception e) {
    			logger.error(e);
    			return false;
    		}
    	}
    	
    	return true;
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
}

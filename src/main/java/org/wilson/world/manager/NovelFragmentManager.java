package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.NovelFragment;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class NovelFragmentManager implements ItemTypeProvider {
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
}

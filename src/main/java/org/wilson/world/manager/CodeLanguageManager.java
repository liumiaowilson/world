package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CodeLanguage;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class CodeLanguageManager implements ItemTypeProvider {
    public static final String NAME = "code_language";
    
    private static CodeLanguageManager instance;
    
    private DAO<CodeLanguage> dao = null;
    
    private List<String> types = new ArrayList<String>();
    
    @SuppressWarnings("unchecked")
    private CodeLanguageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(CodeLanguage.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(CodeLanguage lang : getCodeLanguages()) {
                    boolean found = lang.name.contains(text) || lang.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = lang.id;
                        content.name = lang.name;
                        content.description = lang.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
        
        this.loadTypes();
    }
    
    private void loadTypes() {
    	String typesStr = ConfigManager.getInstance().getConfig("code.language.list", "text,java,javascript,sh,perl,python");
    	if(!StringUtils.isBlank(typesStr)) {
    		String [] items = typesStr.split(",");
    		for(String item : items) {
    			if(!StringUtils.isBlank(item)) {
    				this.types.add(item.trim());
    			}
    		}
    		
    		Collections.sort(this.types);
    	}
    }
    
    public static CodeLanguageManager getInstance() {
        if(instance == null) {
            instance = new CodeLanguageManager();
        }
        return instance;
    }
    
    public void createCodeLanguage(CodeLanguage lang) {
        ItemManager.getInstance().checkDuplicate(lang);
        
        this.dao.create(lang);
    }
    
    public CodeLanguage getCodeLanguage(int id) {
    	CodeLanguage lang = this.dao.get(id);
        if(lang != null) {
            return lang;
        }
        else {
            return null;
        }
    }
    
    public List<CodeLanguage> getCodeLanguages() {
        List<CodeLanguage> result = new ArrayList<CodeLanguage>();
        for(CodeLanguage lang : this.dao.getAll()) {
            result.add(lang);
        }
        return result;
    }
    
    public void updateCodeLanguage(CodeLanguage lang) {
        this.dao.update(lang);
    }
    
    public void deleteCodeLanguage(int id) {
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
        return target instanceof CodeLanguage;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        CodeLanguage lang = (CodeLanguage)target;
        return String.valueOf(lang.id);
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
        
        CodeLanguage lang = (CodeLanguage)target;
        return lang.name;
    }
    
    public List<String> getCodeLanguageTypes() {
    	return Collections.unmodifiableList(this.types);
    }
}

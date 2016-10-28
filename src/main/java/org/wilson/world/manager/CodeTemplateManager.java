package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CodeTemplate;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class CodeTemplateManager implements ItemTypeProvider {
    public static final String NAME = "code_template";
    
    private static CodeTemplateManager instance;
    
    private DAO<CodeTemplate> dao = null;
    
    @SuppressWarnings("unchecked")
    private CodeTemplateManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(CodeTemplate.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(CodeTemplate template : getCodeTemplates()) {
                    boolean found = template.name.contains(text) || template.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = template.id;
                        content.name = template.name;
                        content.description = template.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static CodeTemplateManager getInstance() {
        if(instance == null) {
            instance = new CodeTemplateManager();
        }
        return instance;
    }
    
    public void createCodeTemplate(CodeTemplate template) {
        ItemManager.getInstance().checkDuplicate(template);
        
        this.dao.create(template);
    }
    
    public CodeTemplate getCodeTemplate(int id) {
    	CodeTemplate template = this.dao.get(id);
        if(template != null) {
            return template;
        }
        else {
            return null;
        }
    }
    
    public List<CodeTemplate> getCodeTemplates() {
        List<CodeTemplate> result = new ArrayList<CodeTemplate>();
        for(CodeTemplate template : this.dao.getAll()) {
            result.add(template);
        }
        return result;
    }
    
    public void updateCodeTemplate(CodeTemplate template) {
        this.dao.update(template);
    }
    
    public void deleteCodeTemplate(int id) {
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
        return target instanceof CodeTemplate;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        CodeTemplate template = (CodeTemplate)target;
        return String.valueOf(template.id);
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
        
        CodeTemplate template = (CodeTemplate)target;
        return template.name;
    }
}

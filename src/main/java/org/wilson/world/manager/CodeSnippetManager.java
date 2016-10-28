package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CodeLanguage;
import org.wilson.world.model.CodeSnippet;
import org.wilson.world.model.CodeTemplate;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class CodeSnippetManager implements ItemTypeProvider {
    public static final String NAME = "code_snippet";
    
    private static CodeSnippetManager instance;
    
    private DAO<CodeSnippet> dao = null;
    
    @SuppressWarnings("unchecked")
    private CodeSnippetManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(CodeSnippet.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(CodeSnippet snippet : getCodeSnippets()) {
                    boolean found = snippet.name.contains(text) || snippet.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = snippet.id;
                        content.name = snippet.name;
                        content.description = snippet.name;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static CodeSnippetManager getInstance() {
        if(instance == null) {
            instance = new CodeSnippetManager();
        }
        return instance;
    }
    
    public void createCodeSnippet(CodeSnippet snippet) {
        ItemManager.getInstance().checkDuplicate(snippet);
        
        this.dao.create(snippet);
    }
    
    private CodeSnippet loadCodeSnippet(CodeSnippet snippet) {
    	CodeLanguage lang = CodeLanguageManager.getInstance().getCodeLanguage(snippet.languageId);
    	CodeTemplate template = CodeTemplateManager.getInstance().getCodeTemplate(snippet.templateId);
    	snippet.name = (lang == null ? "None" : lang.name) + " " + (template == null ? "None" : template.name);
    	return snippet;
    }
    
    public CodeSnippet getCodeSnippet(int id) {
    	CodeSnippet snippet = this.dao.get(id);
        if(snippet != null) {
        	snippet = loadCodeSnippet(snippet);
            return snippet;
        }
        else {
            return null;
        }
    }
    
    public List<CodeSnippet> getCodeSnippets() {
        List<CodeSnippet> result = new ArrayList<CodeSnippet>();
        for(CodeSnippet snippet : this.dao.getAll()) {
        	snippet = loadCodeSnippet(snippet);
            result.add(snippet);
        }
        return result;
    }
    
    public void updateCodeSnippet(CodeSnippet snippet) {
        this.dao.update(snippet);
    }
    
    public void deleteCodeSnippet(int id) {
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
        return target instanceof CodeSnippet;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        CodeSnippet snippet = (CodeSnippet)target;
        return String.valueOf(snippet.id);
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
        
        CodeSnippet snippet = (CodeSnippet)target;
        return snippet.name;
    }
}

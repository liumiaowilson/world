package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.code.CodeLanguageDBCleaner;
import org.wilson.world.code.CodeSnippetReport;
import org.wilson.world.code.CodeTemplateDBCleaner;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.CodeLanguage;
import org.wilson.world.model.CodeSnippet;
import org.wilson.world.model.CodeTemplate;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.FormatUtils;

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
        
        ItemManager.getInstance().addDBCleaner(new CodeLanguageDBCleaner());
        ItemManager.getInstance().addDBCleaner(new CodeTemplateDBCleaner());
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
    
    public CodeSnippet getCodeSnippet(int languageId, int templateId) {
    	List<CodeSnippet> snippets = this.getCodeSnippets();
    	for(CodeSnippet snippet : snippets) {
    		if(snippet.languageId == languageId && snippet.templateId == templateId) {
    			return snippet;
    		}
    	}
    	
    	return null;
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
    
    public List<CodeSnippet> getCodeSnippets(int languageId) {
    	List<CodeSnippet> snippets = new ArrayList<CodeSnippet>();
    	
    	for(CodeSnippet snippet : this.getCodeSnippets()) {
    		if(snippet.languageId == languageId) {
    			snippets.add(snippet);
    		}
    	}
    	
    	return snippets;
    }
    
    private boolean usesCodeTemplate(List<CodeSnippet> snippets, int templateId) {
    	for(CodeSnippet snippet : snippets) {
    		if(snippet.templateId == templateId) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public List<CodeTemplate> getMissingCodeTemplates(int languageId) {
    	List<CodeTemplate> ret = new ArrayList<CodeTemplate>();
    	List<CodeTemplate> all = CodeTemplateManager.getInstance().getCodeTemplates();
    	List<CodeSnippet> snippets = this.getCodeSnippets(languageId);
    	for(CodeTemplate template : all) {
    		if(!this.usesCodeTemplate(snippets, template.id)) {
    			ret.add(template);
    		}
    	}
    	
    	return ret;
    }
    
    public List<CodeSnippetReport> getCodeSnippetReports() {
    	List<CodeSnippetReport> reports = new ArrayList<CodeSnippetReport>();
    	
    	List<CodeLanguage> langs = CodeLanguageManager.getInstance().getCodeLanguages();
    	List<CodeTemplate> templates = CodeTemplateManager.getInstance().getCodeTemplates();
    	int total = templates.size();
    	for(CodeLanguage lang : langs) {
    		CodeSnippetReport report = new CodeSnippetReport();
    		report.id = lang.id;
    		report.name = lang.name;
    		List<CodeTemplate> missingList = this.getMissingCodeTemplates(lang.id);
    		int covered = total - missingList.size();
    		report.coverage = FormatUtils.getRoundedValue(covered * 100.0 / total);
    		reports.add(report);
    	}
    	
    	return reports;
    }
}

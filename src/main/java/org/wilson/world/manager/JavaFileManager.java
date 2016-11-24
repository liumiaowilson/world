package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.java.RunJavaInfo;
import org.wilson.world.model.JavaFile;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class JavaFileManager implements ItemTypeProvider {
    public static final String NAME = "java_file";
    
    private static JavaFileManager instance;
    
    private DAO<JavaFile> dao = null;
    
    @SuppressWarnings("unchecked")
    private JavaFileManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(JavaFile.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(JavaFile file : getJavaFiles()) {
                    boolean found = file.name.contains(text) || file.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = file.id;
                        content.name = file.name;
                        content.description = file.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static JavaFileManager getInstance() {
        if(instance == null) {
            instance = new JavaFileManager();
        }
        return instance;
    }
    
    public void createJavaFile(JavaFile file) {
        ItemManager.getInstance().checkDuplicate(file);
        
        RunJavaInfo info = JavaManager.getInstance().compile(file.source);
        if(!info.isSuccessful) {
        	throw new DataException(info.getMessage());
        }
        
        this.dao.create(file);
    }
    
    public JavaFile getJavaFile(int id) {
    	JavaFile file = this.dao.get(id);
        if(file != null) {
            return file;
        }
        else {
            return null;
        }
    }
    
    public List<JavaFile> getJavaFiles() {
        List<JavaFile> result = new ArrayList<JavaFile>();
        for(JavaFile file : this.dao.getAll()) {
            result.add(file);
        }
        return result;
    }
    
    public void updateJavaFile(JavaFile file) {
    	RunJavaInfo info = JavaManager.getInstance().compile(file.source);
        if(!info.isSuccessful) {
        	throw new DataException(info.getMessage());
        }
    	
        this.dao.update(file);
    }
    
    public void deleteJavaFile(int id) {
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
        return target instanceof JavaFile;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        JavaFile file = (JavaFile)target;
        return String.valueOf(file.id);
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
        
        JavaFile file = (JavaFile)target;
        return file.name;
    }
    
    public String getClassName(JavaFile file) {
    	if(file == null) {
    		return null;
    	}
    	
    	return JavaManager.getInstance().getClassName(file.source);
    }
}

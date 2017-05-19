package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

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

    private Map<Integer, JavaFile> cachedJavaFiles = new HashMap<Integer, JavaFile>();
    private Map<Integer, JavaFile> backupJavaFiles = new HashMap<Integer, JavaFile>();
    
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

    public Map<Integer, JavaFile> getCachedJavaFiles() {
        return this.cachedJavaFiles;
    }

    public Map<Integer, JavaFile> getBackupJavaFiles() {
        return this.backupJavaFiles;
    }

    public void createJavaFile(JavaFile file) {
        ItemManager.getInstance().checkDuplicate(file);
        
        RunJavaInfo info = JavaManager.getInstance().compile(file.source);
        if(!info.isSuccessful) {
        	throw new DataException(info.getMessage());
        }
        
        this.dao.create(file);
    }
    
    public void compileAll() {
    	List<String> sources = new ArrayList<String>();
    	
    	for(JavaFile javaFile : this.getJavaFiles()) {
    		sources.add(javaFile.getSource());
    	}
    	
    	RunJavaInfo info = JavaManager.getInstance().compile(sources);
    	if(!info.isSuccessful) {
    		throw new DataException(info.getMessage());
    	}
    }
    
    public JavaFile getJavaFile(int id) {
    	return this.getJavaFile(id, true);
    }

    public JavaFile cloneJavaFile(JavaFile file) {
        if(file == null) return null;

        JavaFile cloned = new JavaFile();
        cloned.id = file.id;
        cloned.name = file.name;
        cloned.description = file.description;
        cloned.source = file.source;
        cloned.script = file.script;

        return cloned;
    }
    
    public JavaFile getJavaFile(int id, boolean lazy) {
        JavaFile file = null;
        if(lazy) {
            file = this.dao.get(id, lazy);
            return file;
        }
        else {
            file = this.cachedJavaFiles.get(id);
            if(file == null) {
                file = this.dao.get(id, lazy);
                file = cloneJavaFile(file);
                this.cachedJavaFiles.put(id, file);
            }

            return cloneJavaFile(file);
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
    	RunJavaInfo info = JavaManager.getInstance().compile(file.getSource());
        if(!info.isSuccessful) {
        	throw new DataException(info.getMessage());
        }
    	
        JavaFile oldJavaFile = this.cachedJavaFiles.remove(file.id);
        if(oldJavaFile != null) {
            this.backupJavaFiles.put(file.id, oldJavaFile);
        }
        this.dao.update(file);
        this.cachedJavaFiles.put(file.id, file);
    }
    
    public void deleteJavaFile(int id) {
        JavaFile oldJavaFile = this.cachedJavaFiles.remove(id);
        if(oldJavaFile != null) {
            this.backupJavaFiles.put(id, oldJavaFile);
        }
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
    	
    	return JavaManager.getInstance().getClassName(file.getSource());
    }
    
    public boolean isLoaded(JavaFile file) {
    	if(file == null) {
    		return false;
    	}
    	
    	return this.dao.isLoaded(file);
    }
    
    public void load(JavaFile file) {
    	if(file == null) {
    		return;
    	}
    	
    	JavaFile loadedFile = this.dao.load(file);
    	file.source = loadedFile.source;
    	file.script = loadedFile.script;
    }
}

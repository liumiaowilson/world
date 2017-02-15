package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.js.JsFileListener;
import org.wilson.world.js.JsFileStatus;
import org.wilson.world.js.TestFailure;
import org.wilson.world.js.TestResult;
import org.wilson.world.model.JsFile;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class JsFileManager implements ItemTypeProvider {
    public static final String NAME = "js_file";
    
    private static JsFileManager instance;
    
    private DAO<JsFile> dao = null;
    
    private List<String> statusList = new ArrayList<String>();
    
    private List<JsFileListener> listeners = new ArrayList<JsFileListener>();
    
    @SuppressWarnings("unchecked")
    private JsFileManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(JsFile.class);
        ((CachedDAO<JsFile>)this.dao).getCache().addCacheListener(new CacheListener<JsFile>() {

			@Override
			public void cachePut(JsFile old, JsFile v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				for(JsFileListener listener : listeners) {
					listener.created(v);
				}
			}

			@Override
			public void cacheDeleted(JsFile v) {
				for(JsFileListener listener : listeners) {
					listener.removed(v);
				}
			}

			@Override
			public void cacheLoaded(List<JsFile> all) {
				for(JsFile file : all) {
					cachePut(null, file);
				}
			}

			@Override
			public void cacheLoading(List<JsFile> old) {
			}
        	
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(JsFile file : getJsFiles()) {
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
        
        this.loadStatusList();
    }
    
    public static JsFileManager getInstance() {
        if(instance == null) {
            instance = new JsFileManager();
        }
        return instance;
    }
    
    public void addJsFileListener(JsFileListener listener) {
    	if(listener != null) {
    		this.listeners.add(listener);
    	}
    }
    
    public void removeJsFileListener(JsFileListener listener) {
    	if(listener != null) {
    		this.listeners.remove(listener);
    	}
    }
    
    private void loadStatusList() {
    	for(JsFileStatus status : JsFileStatus.values()) {
    		this.statusList.add(status.name());
    	}
    }
    
    public void createJsFile(JsFile file) {
        ItemManager.getInstance().checkDuplicate(file);
        
        this.dao.create(file);
    }
    
    public JsFile getJsFile(int id) {
    	return this.getJsFile(id, true);
    }
    
    public JsFile getJsFile(int id, boolean lazy) {
    	JsFile file = this.dao.get(id, lazy);
        if(file != null) {
            return file;
        }
        else {
            return null;
        }
    }
    
    public List<JsFile> getJsFiles() {
        List<JsFile> result = new ArrayList<JsFile>();
        for(JsFile file : this.dao.getAll()) {
            result.add(file);
        }
        return result;
    }
    
    public void updateJsFile(JsFile file) {
        this.dao.update(file);
    }
    
    public void deleteJsFile(int id) {
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
        return target instanceof JsFile;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        JsFile file = (JsFile)target;
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
        
        JsFile file = (JsFile)target;
        return file.name;
    }
    
    public List<String> getStatusList() {
    	return Collections.unmodifiableList(this.statusList);
    }
    
    public String validateJsFile(JsFile file) {
    	if(file == null) {
    		return null;
    	}
    	
    	if(!this.dao.isLoaded(file)) {
    		file = this.dao.load(file);
    	}
    	
    	if(StringUtils.isBlank(file.test)) {
    		return null;
    	}
    	
    	String content = file.source + "\n" + file.test;
    	Map<String, Object> context = new HashMap<String, Object>();
    	TestResult test = new TestResult();
    	context.put("test", test);
    	ScriptManager.getInstance().run(content, context);
    	List<TestFailure> failures = test.getFailures();
    	if(!failures.isEmpty()) {
    		StringBuilder sb = new StringBuilder();
    		for(TestFailure failure : failures) {
    			sb.append(failure.toString()).append("\n");
    		}
    		
    		return sb.toString();
    	}
    	else {
    		return null;
    	}
    }
    
    @SuppressWarnings("unchecked")
	public String getSystemJavaScript() {
    	StringBuilder sb = new StringBuilder();
    	List<String> refs = ReferenceManager.getInstance().getReferenceValues("jsFile");
    	for(String ref : refs) {
    		sb.append(ref).append("\n");
    	}
    	
    	return sb.toString();
    }
}
